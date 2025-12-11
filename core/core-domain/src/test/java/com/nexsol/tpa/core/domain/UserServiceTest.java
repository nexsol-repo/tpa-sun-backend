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

	@Mock
	private UserReader userReader;

	@Mock
	private UserAppender userAppender;

	@Mock
	private EmailVerificationReader emailVerificationReader;

	@Test
	@DisplayName("회원가입 실패: 이메일 인증이 완료되지 않은 경우 예외 발생")
	void signUp_fail_unverified() {
		// given
		NewUser newUser = aNewUser().build();

		// [Fixture] 인증 안 된 상태(isVerified=false) 생성
		EmailVerification unverifiedEmail = aVerification().email(newUser.applicantEmail()).isVerified(false).build();

		given(emailVerificationReader.read(newUser.companyCode(), newUser.applicantEmail(), EmailVerifiedType.SIGNUP))
			.willReturn(unverifiedEmail);

		// when & then
		assertThatThrownBy(() -> userService.signUp(newUser)).isInstanceOf(CoreException.class)
			.extracting("errorType")
			.isEqualTo(CoreErrorType.EMAIL_VERIFIED_AUTH);

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
		EmailVerification expiredVerification = aVerification().email(newUser.applicantEmail())
			.isVerified(true)
			.verifiedAt(past)
			.build();

		given(emailVerificationReader.read(newUser.companyCode(), newUser.applicantEmail(), EmailVerifiedType.SIGNUP))
			.willReturn(expiredVerification);

		// when & then
		assertThatThrownBy(() -> userService.signUp(newUser)).isInstanceOf(CoreException.class)
			.extracting("errorType")
			.isEqualTo(CoreErrorType.EMAIL_VERIFIED_OVERTIME);
	}

}
