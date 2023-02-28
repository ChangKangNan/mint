package org.ckn.jwt.endpoint;

import cn.hutool.json.JSONUtil;
import org.ckn.util.ApiResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 被ExceptionTranslationFilter用来作为认证方案的入口，即当用户请求处理过程中遇见认证异常时，被异常处理器（ExceptionTranslationFilter）用来开启特定的认证流程
 */
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String reason = authException.getMessage();
        response.getWriter().write(JSONUtil.toJsonStr(ApiResult.customize(HttpServletResponse.SC_FORBIDDEN, null, reason)));
    }
}
