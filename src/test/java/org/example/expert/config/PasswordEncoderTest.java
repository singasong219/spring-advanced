package org.example.expert.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderTest {

    // @InjectMocks는 Mock 객체를 주입할 때 쓰는 건데, PasswordEncoder는 실제 객체가 필요함
    // @Spy는 실제 객체를 사용하면서 Mockito가 관리할 수 있게 해줌

    @Spy
    private PasswordEncoder passwordEncoder = new PasswordEncoder();

    @Test
    void matches_메서드가_정상적으로_동작한다() {
        // given
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // rawPassword는 원본비밀번호, encodedPassword는 암호화된 비밀번호이다.
        // when
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // then
        assertTrue(matches);
    }
}
