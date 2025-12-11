package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationReader {

	private final EmailVerificationRepository emailVerificationRepository;

	public EmailVerification read(String companyCode,String email, EmailVerifiedType type) {
		return emailVerificationRepository.findByCompanyCodeAndEmailAndType(companyCode,email, type)
			.orElseThrow(() -> new CoreException(CoreErrorType.EMAIL_VERIFIED_EMPTY));
	}

}
