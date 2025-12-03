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
import java.util.Optional;

import static com.nexsol.tpa.core.domain.EmailVerificationFixture.aVerification;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailVerifiedServiceTest {

	@InjectMocks
	private EmailVerifiedService emailVerifiedService;

	@Mock
	private EmailSender emailSender;

	@Mock
	private EmailGenerateCode emailGenerateCode;

	@Mock
	private EmailVerificationFinder emailVerificationFinder;

	@Mock
	private EmailVerificationAppender emailVerificationAppender;

	@Test
	@DisplayName("인증코드 발송 성공: 1분 쿨타임이 지났거나 처음이면 발송된다")
	void sendCode_success() {
		// given
		String email = "test@nexsol.com";
		String newCode = "123456";

		// Finder는 빈 값 반환 (처음 요청)
		given(emailVerificationFinder.find(email, EmailVerifiedType.SIGNUP)).willReturn(Optional.empty());

		// 코드 생성기 Mocking
		given(emailGenerateCode.generateCode()).willReturn(newCode);

		// when
		emailVerifiedService.sendCode(email, EmailVerifiedType.SIGNUP);

		// then

		verify(emailVerificationAppender).append(any(EmailVerification.class));
		verify(emailSender).send(email, newCode);
	}

	@Test
	@DisplayName("인증코드 발송 실패: 1분 내에 재요청하면 예외 발생")
	void sendCode_fail_cool_time() {
		// given
		String email = "test@nexsol.com";
		LocalDateTime now = LocalDateTime.now();

		// 30초 전에 보낸 기록이 있음
		EmailVerification recentVerification = aVerification().sentAt(now.minusSeconds(30)).build();

		given(emailVerificationFinder.find(email, EmailVerifiedType.SIGNUP))
			.willReturn(Optional.of(recentVerification));

		// when & then
		assertThatThrownBy(() -> emailVerifiedService.sendCode(email, EmailVerifiedType.SIGNUP))
			.isInstanceOf(CoreException.class)
			.extracting("errorType")
			.isEqualTo(CoreErrorType.EMAIL_VERIFIED_REPEAT);
	}

}
