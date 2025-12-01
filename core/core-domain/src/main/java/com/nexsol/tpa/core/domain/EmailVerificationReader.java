package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationReader {
    private final EmailVerificationRepository emailVerificationRepository;

    public void ensureVerified(String email) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email).orElseThrow(() -> new CoreException(CoreErrorType.INVALID_INPUT, "인증 요청 내역이 없습니다."));

        if (!verification.isVerified()) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "이메일 인증이 완료되지 않았습니다.");
        }
    }
}
