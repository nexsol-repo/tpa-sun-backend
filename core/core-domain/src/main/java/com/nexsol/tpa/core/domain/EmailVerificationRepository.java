package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;

import java.util.Optional;

public interface EmailVerificationRepository {

	EmailVerification save(EmailVerification verification);

	Optional<EmailVerification> findByCompanyCodeAndEmailAndType(String companyCode, String email,
			EmailVerifiedType type);

}
