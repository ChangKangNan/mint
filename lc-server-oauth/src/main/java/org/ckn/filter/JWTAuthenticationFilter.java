package org.ckn.filter;

import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ckn.entity.AuthUser;
import org.ckn.util.ApiResult;
import org.ckn.util.JWTConstants;
import org.ckn.util.Md5Utils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * 过期时间2小时
     */
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 2;

    private AuthenticationManager authenticationManager;

    private StringRedisTemplate redisTemplate;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, StringRedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        AuthUser user = (AuthUser) authResult.getPrincipal();
        log.info("user-getPrincipal:{}", JSONUtil.toJsonStr(user));
        Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities();
        /**user
         * 1、创建密钥
         */
        MACSigner macSigner = new MACSigner(JWTConstants.SECRET);
        /**
         * 2、payload
         */
        String payload = JSONUtil.toJsonStr(user);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("subject")
                .claim("payload", payload)
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .build();
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        /**
         * 创建签名的JWT
         */
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
        signedJWT.sign(macSigner);
        /**
         * 生成token
         */
        String jwtToken = signedJWT.serialize();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiResult result = ApiResult.success(jwtToken);
        //生成Key， 把权限放入到redis中
        String keyPrefix = "JWT" + user.getId() + ":";
        String keySuffix = Md5Utils.getMD5(jwtToken.getBytes());
        String key = keyPrefix + keySuffix;

        String authKey = key + ":Authorities";

        redisTemplate.opsForValue().set(key, jwtToken, EXPIRE_TIME, TimeUnit.MILLISECONDS);
        List<String> authList = grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        redisTemplate.opsForValue().set(authKey, JSONUtil.toJsonStr(authList), EXPIRE_TIME, TimeUnit.SECONDS);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.error("登录认证失败", failed);
        ApiResult result = null;
        int status = 401;
        if (failed instanceof UsernameNotFoundException) {
            result = ApiResult.customize(404, null, "用户不存在");
        } else if (failed instanceof BadCredentialsException) {
            result = ApiResult.customize(401, null, "用户名密码错误");
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}