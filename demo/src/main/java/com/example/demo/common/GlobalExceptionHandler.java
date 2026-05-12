package com.example.demo.common;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception: ", e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("Exception: ", e);
        return Result.fail("系统错误: " + e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Result<?> handleExpiredJwt(ExpiredJwtException e) {
        return Result.unauthorized("token已过期");
    }

    @ExceptionHandler(JwtException.class)
    public Result<?> handleJwt(JwtException e) {
        return Result.unauthorized("token无效");
    }
}
