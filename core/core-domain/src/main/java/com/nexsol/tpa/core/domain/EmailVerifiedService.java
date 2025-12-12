package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerifiedService {

	private final EmailSender emailSender;

	private final EmailGenerateCode emailGenerateCode;

	private final EmailVerificationReader emailVerificationReader;

	private final EmailVerificationFinder emailVerificationFinder;

	private final EmailSendValidator emailSendValidator;

	private final EmailVerificationAppender emailVerificationAppender;

	public void sendCode(String companyCode, String email, EmailVerifiedType type) {

		emailSendValidator.validate(companyCode, email, type);

		LocalDateTime now = LocalDateTime.now();

		emailVerificationFinder.find(companyCode, email, type).ifPresent(exist -> exist.validateResend(now));

		String newCode = emailGenerateCode.generateCode();

		EmailVerification verification = EmailVerification.builder()
			.companyCode(companyCode)
			.email(email)
			.code(newCode)
			.isVerified(false)
			.verifiedType(type)
			.attemptCount(0)
			.sentAt(now)
			.expiredAt(now.plusMinutes(5))
			.build();

		emailVerificationAppender.append(verification);

		emailSender.send(email, newCode);
	}

	public void verifyCode(String companyCode, String email, String code, EmailVerifiedType type) {
		LocalDateTime now = LocalDateTime.now();

		EmailVerification verification = emailVerificationReader.read(companyCode, email, type);

		EmailVerification verified = verification.verify(code, now);

		emailVerificationAppender.append(verified);
	}

}
