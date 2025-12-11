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
import static com.nexsol.tpa.core.domain.NewUserBuilder.aNewUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
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
	private EmailSendValidator emailSendValidator;

	@Mock
	private EmailVerificationAppender emailVerificationAppender;

	@Test
	@DisplayName("인증코드 전송 실패: 이미 존재하는 유저(사업자번호/이메일)인 경우 예외 발생")
	void sendCode_fail_duplicate() {
		// given
		String companyCode = "1234567890"; // 테스트용 사업자번호
		String email = "exist_user@example.com";
		EmailVerifiedType type = EmailVerifiedType.SIGNUP;

		// Validator가 유효성 검사 시 '이미 존재하는 데이터' 예외를 발생시키도록 Mocking
		willThrow(new CoreException(CoreErrorType.USER_EXIST_DATA)).given(emailSendValidator)
			.validate(companyCode, email, type);

		// when & then
		// sendCode 호출 시 Validator에 의해 예외가 발생하는지 검증
		assertThatThrownBy(() -> emailVerifiedService.sendCode(companyCode, email, type))
			.isInstanceOf(CoreException.class)
			.extracting("errorType")
			.isEqualTo(CoreErrorType.USER_EXIST_DATA);
	}

	@Test
	@DisplayName("인증코드 발송 성공: 1분 쿨타임이 지났거나 처음이면 발송된다")
	void sendCode_success() {
		// given
		String companyCode = "123-45-67890";
		String email = "test@nexsol.com";
		String newCode = "123456";

		doNothing().when(emailSendValidator).validate(companyCode, email, EmailVerifiedType.SIGNUP);
		// Finder는 빈 값 반환 (처음 요청)
		given(emailVerificationFinder.find(companyCode,email, EmailVerifiedType.SIGNUP)).willReturn(Optional.empty());

		// 코드 생성기 Mocking
		given(emailGenerateCode.generateCode()).willReturn(newCode);

		// when
		emailVerifiedService.sendCode(companyCode, email, EmailVerifiedType.SIGNUP);

		// then
		verify(emailSendValidator).validate(companyCode, email, EmailVerifiedType.SIGNUP);
		verify(emailSender).send(email, newCode);

	}

	@Test
	@DisplayName("인증코드 발송 실패: 1분 내에 재요청하면 예외 발생")
	void sendCode_fail_cool_time() {
		// given
		String companyCode = "123-45-67890";
		String email = "test@nexsol.com";
		LocalDateTime now = LocalDateTime.now();

		// 30초 전에 보낸 기록이 있음
		EmailVerification recentVerification = aVerification().sentAt(now.minusSeconds(30)).build();

		given(emailVerificationFinder.find(companyCode,email, EmailVerifiedType.SIGNUP))
			.willReturn(Optional.of(recentVerification));

		// when & then
		assertThatThrownBy(() -> emailVerifiedService.sendCode(companyCode, email, EmailVerifiedType.SIGNUP))
			.isInstanceOf(CoreException.class)
			.extracting("errorType")
			.isEqualTo(CoreErrorType.EMAIL_VERIFIED_REPEAT);
	}

}
