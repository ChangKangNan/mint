package org.ckn.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.ckn.util.JWTConstants;
import org.ckn.util.SecurityUtils;
import org.ckn.util.UserVo;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @author ckn
 * @date 2023/3/1
 */
@Slf4j
@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(JWTConstants.TOKEN_HEADER);
        log.info("token:"+token);
        if (StrUtil.isNotBlank(token)) {
            String relToken = token.replace(JWTConstants.TOKEN_PREFIX, "");
            SignedJWT jwt = SignedJWT.parse(relToken);
            JWSVerifier verifier = new MACVerifier(JWTConstants.SECRET);
            //校验是否有效
            if (jwt.verify(verifier)) {
                //校验超时
                Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
                if (!new Date().after(expirationTime)) {
                    Object payload = jwt.getJWTClaimsSet().getClaim("payload");
                    UserVo user = JSONUtil.toBean(payload.toString(), UserVo.class);
                    Map map = JSONUtil.toBean(JSONUtil.toJsonStr(user), Map.class);
                    SecurityUtils.getInstance().setContext(map);
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        SecurityUtils.getInstance().clear();
    }
}
