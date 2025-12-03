package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailVerificationFinder {

	private final EmailVerificationRepository emailVerificationRepository;

	public Optional<EmailVerification> find(String email, EmailVerifiedType type) {
		return emailVerificationRepository.findByEmailAndType(email, type);
	}

}
