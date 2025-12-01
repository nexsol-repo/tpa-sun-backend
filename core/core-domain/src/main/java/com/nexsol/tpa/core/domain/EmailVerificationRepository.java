package com.nexsol.tpa.core.domain;

import java.util.Optional;

public interface EmailVerificationRepository {
    EmailVerification save(EmailVerification verification);

    Optional<EmailVerification> findByEmail(String email);
}
