package org.example.expert.config;

import org.example.expert.domain.common.exception.AuthException;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthInterceptorTest {

    @InjectMocks
    private AdminAuthInterceptor adminAuthInterceptor;

    @Test
    void 어드민_권한이_있으면_true를_반환한다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("userRole", UserRole.ADMIN.name());

        // when
        boolean result = adminAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
    }

    @Test
    void 어드민_권한이_없으면_예외가_발생한다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("userRole", UserRole.USER.name());

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> adminAuthInterceptor.preHandle(request, response, new Object()));
        assertEquals("관리자 권한이 없습니다.", exception.getMessage());
    }
}