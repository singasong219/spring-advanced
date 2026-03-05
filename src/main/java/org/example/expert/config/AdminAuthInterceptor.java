package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.common.exception.AuthException;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userRole = (String) request.getAttribute("userRole");

        if (!UserRole.ADMIN.name().equals(userRole)) {
            throw new AuthException("관리자 권한이 없습니다.");

        }

        log.info("[ADMIN ACCESS] 요청시각={}, URL={}", LocalDateTime.now(), request.getRequestURI());
        return true;
    }
}
