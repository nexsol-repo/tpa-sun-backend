package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;

public record NewUser (
        String companyCode,
        String email,
        String companyName,
        String name,
        String phoneNumber,
        String applicantName,
        String applicantEmail,
        String applicantPhoneNumber,
        boolean termsAgreed,
        boolean privacyAgreed
){

    public NewUser {
        // 생성자 내 검증 (Compact Constructor)
        if (!termsAgreed || !privacyAgreed) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "필수 약관에 동의해야 합니다.");
        }
        if (companyCode == null || companyCode.isBlank()) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "사업자 번호는 필수입니다.");
        }
    }

    public User toUser() {
        return User.builder()
                .companyCode(companyCode)
                .email(email)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .applicantName(applicantName)
                .applicantEmail(applicantEmail)
                .applicantPhoneNumber(applicantPhoneNumber)
                .build();
    }
}
