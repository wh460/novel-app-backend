package com.cafuc.novelapp.config;

import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.result.ResultCode;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(JwtException.class)
    public Result<?> jwtError() {
        return Result.fail(ResultCode.TOKEN_INVALID);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> allError(Exception e) {
        // 打印详细的异常信息
        logger.error("服务器异常: ", e);
        return Result.fail(ResultCode.FAIL);
    }
}