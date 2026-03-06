package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void 유저_조회_성공() {
        // given
        User user = new User("test@test.com", "encodedPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(1L);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test@test.com", response.getEmail());
    }

    @Test
    void 유저_조회_시_존재하지_않으면_예외가_발생한다() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.getUser(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void 비밀번호_변경_성공() {
        // given
        User user = new User("test@test.com", "encodedOldPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        UserChangePasswordRequest request = new UserChangePasswordRequest("OldPassword1!", "NewPassword1!");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("NewPassword1!", "encodedOldPassword")).willReturn(false);
        given(passwordEncoder.matches("OldPassword1!", "encodedOldPassword")).willReturn(true);
        given(passwordEncoder.encode("NewPassword1!")).willReturn("encodedNewPassword");

        // when & then
        assertDoesNotThrow(() -> userService.changePassword(1L, request));
    }

    @Test
    void 비밀번호_변경_시_유저가_없으면_예외가_발생한다() {
        // given
        UserChangePasswordRequest request = new UserChangePasswordRequest("OldPassword1!", "NewPassword1!");
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(1L, request));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void 새_비밀번호가_기존_비밀번호와_같으면_예외가_발생한다() {
        // given
        User user = new User("test@test.com", "encodedPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        UserChangePasswordRequest request = new UserChangePasswordRequest("SamePassword1!", "SamePassword1!");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("SamePassword1!", "encodedPassword")).willReturn(true);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(1L, request));
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 기존_비밀번호가_틀리면_예외가_발생한다() {
        // given
        User user = new User("test@test.com", "encodedPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        UserChangePasswordRequest request = new UserChangePasswordRequest("WrongOld1!", "NewPassword1!");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("NewPassword1!", "encodedPassword")).willReturn(false);
        given(passwordEncoder.matches("WrongOld1!", "encodedPassword")).willReturn(false);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(1L, request));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
}