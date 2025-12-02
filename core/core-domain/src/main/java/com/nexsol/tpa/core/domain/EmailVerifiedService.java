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
    private final EmailVerificationFinder emailVerificationFinder;
    private final EmailVerificationAppender emailVerificationAppender;


    public void sendCode(String email, EmailVerifiedType type) {
        LocalDateTime now = LocalDateTime.now();

        emailVerificationFinder.find(email, type).ifPresent(exist -> exist.validateResend(now));

        String newCode = emailGenerateCode.generateCode();

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(newCode)
                .isVerified(false)      // 다시 미인증 상태로
                .verifiedType(type)
                .attemptCount(0)        // 횟수 초기화 (필요시 로직 변경 가능)
                .sentAt(now)
                .expiredAt(now.plusMinutes(5)) // 5분 유효
                .build();

        emailVerificationAppender.append(verification);
    }
}
