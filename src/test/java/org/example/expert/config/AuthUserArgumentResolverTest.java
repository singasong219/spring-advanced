package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthUserArgumentResolverTest {

    @InjectMocks
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Mock
    private MethodParameter parameter;

    @Test
    void Auth_어노테이션과_AuthUser_타입이_모두_있으면_true를_반환한다() {
        // given
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(org.mockito.Mockito.mock(Auth.class));
        given(parameter.getParameterType()).willReturn((Class) AuthUser.class);

        // when
        boolean result = authUserArgumentResolver.supportsParameter(parameter);

        // then
        assertTrue(result);
    }

    @Test
    void Auth_어노테이션도_없고_AuthUser_타입도_아니면_false를_반환한다() {
        // given
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(null);
        given(parameter.getParameterType()).willReturn((Class) String.class);

        // when
        boolean result = authUserArgumentResolver.supportsParameter(parameter);

        // then
        assertFalse(result);
    }

    @Test
    void Auth_어노테이션만_있고_AuthUser_타입이_아니면_예외가_발생한다() {
        // given
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(org.mockito.Mockito.mock(Auth.class));
        given(parameter.getParameterType()).willReturn((Class) String.class);

        // when & then
        assertThrows(AuthException.class,
                () -> authUserArgumentResolver.supportsParameter(parameter));
    }

    @Test
    void AuthUser_타입이지만_Auth_어노테이션이_없으면_예외가_발생한다() {
        // given
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(null);
        given(parameter.getParameterType()).willReturn((Class) AuthUser.class);

        // when & then
        assertThrows(AuthException.class,
                () -> authUserArgumentResolver.supportsParameter(parameter));
    }

    @Test
    void resolveArgument_정상_동작() {
        // given
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setAttribute("userId", 1L);
        httpRequest.setAttribute("email", "test@test.com");
        httpRequest.setAttribute("userRole", "USER");

        NativeWebRequest webRequest = new ServletWebRequest(httpRequest);

        // when
        Object result = authUserArgumentResolver.resolveArgument(null, null, webRequest, null);

        // then
        assertNotNull(result);
        AuthUser authUser = (AuthUser) result;
        assertEquals(1L, authUser.getId());
        assertEquals("test@test.com", authUser.getEmail());
        assertEquals(UserRole.USER, authUser.getUserRole());
    }
}
