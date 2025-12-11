package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmailVerification(String companyCode, String email, String code, boolean isVerified, int attemptCount,
                                EmailVerifiedType verifiedType, LocalDateTime sentAt, LocalDateTime expiredAt,
                                LocalDateTime verifiedAt) {

    public EmailVerification verify(String inputCode, LocalDateTime now) {
        if (expiredAt.isBefore(now)) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_OVERTIME);
        }
        if (!this.code.equals(inputCode)) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_INVALID);
        }

        return EmailVerification.builder()
                .companyCode(this.companyCode)
                .email(this.email)
                .code(this.code)
                .isVerified(true)
                .attemptCount(this.attemptCount)
                .verifiedType(this.verifiedType)
                .sentAt(this.sentAt)
                .expiredAt(this.expiredAt)
                .verifiedAt(now)
                .build();
    }

    public void validateSignup(LocalDateTime now) {

        if (!this.isVerified) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_AUTH);
        }

        if (this.verifiedAt.isBefore(now.minusMinutes(10))) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_OVERTIME);
        }
    }

    public void validateUpdate(LocalDateTime now) {

        if (!this.isVerified) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_AUTH);
        }

        if (this.verifiedAt.isBefore(now.minusMinutes(10))) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_OVERTIME);
        }
    }

    public void checkCodeForLogin(String inputCode, LocalDateTime now) {
        if (this.expiredAt.isBefore(now)) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_OVERTIME);
        }

        if (!this.code.equals(inputCode)) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_INVALID);
        }
    }

    public void validateResend(LocalDateTime now) {
        if (sentAt.isAfter(now.minusMinutes(1))) {
            throw new CoreException(CoreErrorType.EMAIL_VERIFIED_REPEAT);
        }
    }
}