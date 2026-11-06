package com.cafuc.novelapp.config;

import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.result.ResultCode;
import com.cafuc.novelapp.util.JwtUtil;
import com.cafuc.novelapp.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtInterceptor(JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.contains("/user/login")
                || uri.contains("/user/register")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/chapter")
                || uri.startsWith("/book")
                || uri.startsWith("/bookshelf")
                || uri.startsWith("/readingProgress")
                || uri.startsWith("/webjars")
                || uri.equals("/doc.html")
                || uri.startsWith("/covers")
                || uri.startsWith("/announcement")
                || uri.startsWith("/admin")
                || uri.equals("/favicon.ico")) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
            writeErr(response);
            return false;
        }
        token = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);
        String redisToken = (String) redisUtil.get("user:token:" + userId);
        if (!token.equals(redisToken)) {
            writeErr(response);
            return false;
        }
        request.setAttribute("userId", userId);
        return true;
    }

    private void writeErr(HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(ResultCode.TOKEN_INVALID)));
    }


}
