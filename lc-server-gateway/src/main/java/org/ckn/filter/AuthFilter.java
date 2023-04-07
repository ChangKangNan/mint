package org.ckn.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.ckn.config.ExclusionUrl;
import org.ckn.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Yangxiufeng
 * @date 2020-10-29
 * @time 9:29
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter , Ordered {

    @Autowired
    private ExclusionUrl exclusionUrl;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String headerToken = request.getHeaders().getFirst(JWTConstants.TOKEN_HEADER);
        String requestURI = request.getURI().getPath();
        log.info("headerToken:{}", headerToken);
        //1、只要带上了token， 就需要判断Token是否有效
        if(StrUtil.startWith(requestURI,"/user/login")){
           return chain.filter(exchange);
        }
        String path = request.getURI().getPath();
        log.info("request path:{}", path);
        //2、判断是否是过滤的路径， 是的话就放行
        if (isExclusionUrl(path) ){
            return chain.filter(exchange);
        }
        if (StringUtils.isEmpty(headerToken)){
            return getVoidMono(response, 401, "未携带token");
        }
        if ( !StringUtils.isEmpty(headerToken) && !verifierToken(headerToken)){
            return getVoidMono(response, 401, "token无效");
        }
        //3、判断请求的URL是否有权限
        boolean permission = hasPermission(headerToken , path);
        if (!permission){
            return getVoidMono(response, 403, "无访问权限");
        }
        //放置头部信息
        // ServerHttpRequest tokenRequest = request.mutate()
        ////将灰度标记传递过去  param: version  value: 2.0
        // .header("version","2.0")
        // .build();
        // ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> getVoidMono(ServerHttpResponse response, int i, String msg) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);
        ApiResult failed = ApiResult.customize(i, msg);
        byte[] bits = JSONUtil.toJsonStr(failed).getBytes();
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        return response.writeWith(Mono.just(buffer));
    }

    private boolean isExclusionUrl(String path){
        List<String> exclusions = exclusionUrl.getUrl();
        if(exclusions !=null){
            if (exclusions.size() == 0){
                return false;
            }
            return exclusions.stream().anyMatch( action -> antPathMatcher.match(action , path));
        }
        return false;
    }

    private boolean verifierToken(String headerToken){
        try {
            SignedJWT jwt = getSignedJWT(headerToken);
            JWSVerifier verifier = new MACVerifier(JWTConstants.SECRET);
            //校验是否有效
            if (!jwt.verify(verifier)) {
                log.error("token不合法，检测不过关");
                return false;
            }
            //校验超时
            Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
            if (new Date().after(expirationTime)) {
                log.error("token已经过期");
                return false;
            }
            //获取载体中的数据
            return true;
        } catch (ParseException | JOSEException e) {
            log.error("token校验出错",e);
        }
        return false;
    }
    private boolean hasPermission(String headerToken, String path){
        try {
            if (StringUtils.isEmpty(headerToken)){
                return false;
            }
            SignedJWT jwt = getSignedJWT(headerToken);
            Object payload = jwt.getJWTClaimsSet().getClaim("payload");
            UserVo user = JSONUtil.toBean(payload.toString(), UserVo.class);
            String keyPrefix = "JWT" + user.getId() + ":";
            String token = headerToken.replace(JWTConstants.TOKEN_PREFIX, "");
            String keySuffix = Md5Utils.getMD5(token.getBytes());
            String key = keyPrefix + keySuffix;
            String authKey = key + ":Authorities";

            String authStr = redisTemplate.opsForValue().get(authKey);
            if (StringUtils.isEmpty(authStr)){
                return false;
            }
            List<String> authorities = JSONUtil.toList(authStr , String.class);
            return authorities.stream().anyMatch(authority -> antPathMatcher.match(authority, path));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    private SignedJWT getSignedJWT(String headerToken) throws ParseException {
        String token = headerToken.replace(JWTConstants.TOKEN_PREFIX, "");
        log.info("token is {}", token);
        return SignedJWT.parse(token);
    }
}
