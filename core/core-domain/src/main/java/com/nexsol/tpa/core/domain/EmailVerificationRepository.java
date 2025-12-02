package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;

import java.util.Optional;

public interface EmailVerificationRepository {
    EmailVerification save(EmailVerification verification);

    Optional<EmailVerification> findByEmail(String email);

    Optional<EmailVerification> findByEmailAndType(String email, EmailVerifiedType type);
}
