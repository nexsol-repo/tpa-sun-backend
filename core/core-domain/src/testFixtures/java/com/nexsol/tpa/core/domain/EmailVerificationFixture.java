package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import java.time.LocalDateTime;

public class EmailVerificationFixture {

    // 기본적으로 "인증 완료된" 유효한 상태를 반환
    public static EmailVerification.EmailVerificationBuilder aVerification() {
        LocalDateTime now = LocalDateTime.now();
        return EmailVerification.builder()
                .email("test@nexsol.com")
                .code("123456")
                .isVerified(true)
                .verifiedType(EmailVerifiedType.SIGNUP)
                .attemptCount(0)
                .sentAt(now.minusMinutes(1))
                .verifiedAt(now) // 기본: 방금 인증함
                .expiredAt(now.plusMinutes(4));
    }
}