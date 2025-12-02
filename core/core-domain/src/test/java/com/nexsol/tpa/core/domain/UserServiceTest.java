package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;

import static com.nexsol.tpa.core.domain.EmailVerificationFixture.aVerification;
import static com.nexsol.tpa.core.domain.NewUserBuilder.aNewUser;
import static com.nexsol.tpa.core.domain.UserFixture.aUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock private UserReader userReader;
    @Mock private UserAppender userAppender;
    @Mock private EmailVerificationReader emailVerificationReader;

    @Test
    @DisplayName("회원가입 성공: 인증된 이메일이고 중복이 없으면 회원이 저장된다")
    void signUp_success() {
        //given
        NewUser newUser = aNewUser().withCompanyCode("111-11-11111").build();
        User expectedUser = aUser().id(1L).companyCode("111-11-11111").build();
        EmailVerification verifiedEmail = aVerification()
                .email(newUser.email())
                .build();

        given(emailVerificationReader.read(newUser.email(), EmailVerifiedType.SIGNUP))
                .willReturn(verifiedEmail);

        given(userAppender.append(any())).willReturn(expectedUser);

        //when
        User result = userService.signUp(newUser);

        //then
        assertThat(result.companyCode()).isEqualTo(newUser.companyCode());
        assertThat(result.id()).isEqualTo(1L);

        verify(userReader).exist(newUser.companyCode(), newUser.email());
        verify(userAppender).append(any(User.class));

    }

    @Test
    @DisplayName("회원가입 실패: 이메일 인증이 완료되지 않은 경우 예외 발생")
    void signUp_fail_unverified() {
        // given
        NewUser newUser = aNewUser().build();

        // [Fixture] 인증 안 된 상태(isVerified=false) 생성
        EmailVerification unverifiedEmail = aVerification()
                .email(newUser.email())
                .isVerified(false)
                .build();

        given(emailVerificationReader.read(newUser.email(), EmailVerifiedType.SIGNUP))
                .willReturn(unverifiedEmail);

        // when & then
        assertThatThrownBy(() -> userService.signUp(newUser))
                .isInstanceOf(CoreException.class)
                .extracting("errorType").isEqualTo(CoreErrorType.EMAIL_VERIFIED_AUTH);

        // (중복 체크나 저장이 호출되지 않아야 함)
        // verify(userReader, never()).exist(any(), any());
    }

    @Test
    @DisplayName("회원가입 실패: 인증 후 유효 시간(60분)이 지난 경우 예외 발생")
    void signUp_fail_timeout() {
        // given
        NewUser newUser = aNewUser().build();
        LocalDateTime past = LocalDateTime.now().minusMinutes(61); // 61분 전

        // [Fixture] 인증은 됐지만 시간이 지난 상태
        EmailVerification expiredVerification = aVerification()
                .email(newUser.email())
                .isVerified(true)
                .verifiedAt(past)
                .build();

        given(emailVerificationReader.read(newUser.email(), EmailVerifiedType.SIGNUP))
                .willReturn(expiredVerification);

        // when & then
        assertThatThrownBy(() -> userService.signUp(newUser))
                .isInstanceOf(CoreException.class)
                .extracting("errorType").isEqualTo(CoreErrorType.EMAIL_VERIFIED_OVERTIME);
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 유저(사업자번호/이메일)인 경우 예외 발생")
    void signUp_fail_duplicate() {
        // given
        NewUser newUser = aNewUser().build();
        EmailVerification verifiedEmail = aVerification().email(newUser.email()).build();

        given(emailVerificationReader.read(newUser.email(), EmailVerifiedType.SIGNUP))
                .willReturn(verifiedEmail);


        willThrow(new CoreException(CoreErrorType.USER_EXIST_DATA))
                .given(userReader).exist(newUser.companyCode(), newUser.email());

        // when & then
        assertThatThrownBy(() -> userService.signUp(newUser))
                .isInstanceOf(CoreException.class)
                .extracting("errorType").isEqualTo(CoreErrorType.USER_EXIST_DATA);
    }
}

