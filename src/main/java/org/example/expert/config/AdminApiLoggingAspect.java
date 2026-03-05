package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class AdminApiLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(AdminApiLoggingAspect.class);

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..)) || execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))")
    public Object logAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        Long userId = (Long) request.getAttribute("userId");
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();
        String requestBody = Arrays.toString(joinPoint.getArgs());

        Object result = joinPoint.proceed();

        String responseBody = result != null ? result.toString() : "void";

        log.info("[ADMIN API] 사용자ID={}, 요청시각={}, URL={}, 요청본문={}, 응답본문={}",
                userId, requestTime, requestUrl, requestBody, responseBody);

        return result;
    }
}