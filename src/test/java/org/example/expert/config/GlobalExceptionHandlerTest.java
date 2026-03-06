package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void InvalidRequestException_처리() {
        // given
        InvalidRequestException ex = new InvalidRequestException("잘못된 요청입니다.");

        // when
        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.invalidRequestExceptionException(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BAD_REQUEST", response.getBody().get("status"));
        assertEquals(400, response.getBody().get("code"));
        assertEquals("잘못된 요청입니다.", response.getBody().get("message"));
    }

    @Test
    void AuthException_처리() {
        // given
        AuthException ex = new AuthException("인증 오류입니다.");

        // when
        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleAuthException(ex);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("UNAUTHORIZED", response.getBody().get("status"));
        assertEquals(401, response.getBody().get("code"));
        assertEquals("인증 오류입니다.", response.getBody().get("message"));
    }

    @Test
    void ServerException_처리() {
        // given
        ServerException ex = new ServerException("서버 오류입니다.");

        // when
        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleServerException(ex);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().get("status"));
        assertEquals(500, response.getBody().get("code"));
        assertEquals("서버 오류입니다.", response.getBody().get("message"));
    }
}