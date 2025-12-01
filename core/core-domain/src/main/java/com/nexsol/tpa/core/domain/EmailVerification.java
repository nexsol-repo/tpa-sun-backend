package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmailVerification(
        String email,
        String code,
        boolean isVerified,
        int attemptCount,
        LocalDateTime sentAt,
        LocalDateTime expiredAt
) {

    public EmailVerification verify(String inputCode, LocalDateTime now) {
        if (expiredAt.isBefore(now)) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "인증 시간이 만료되었습니다.");
        }
        if (!this.code.equals(inputCode)) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "인증 코드가 일치하지 않습니다.");
        }

        return EmailVerification.builder()
                .email(this.email)
                .code(this.code)
                .isVerified(true)
                .attemptCount(this.attemptCount)
                .sentAt(this.sentAt)
                .expiredAt(this.expiredAt)
                .build();
    }


    public void validateResend(LocalDateTime now) {
        if (sentAt.isAfter(now.minusMinutes(1))) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "잠시 후 다시 시도해주세요.");
        }
    }
}