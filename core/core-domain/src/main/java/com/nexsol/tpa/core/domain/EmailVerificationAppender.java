package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationAppender {

	private final EmailVerificationRepository emailVerificationRepository;

	public EmailVerification append(EmailVerification verification) {
		return emailVerificationRepository.save(verification);
	}

}
