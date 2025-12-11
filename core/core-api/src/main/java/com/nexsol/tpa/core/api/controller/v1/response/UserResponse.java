package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.User;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

@Builder
public record UserResponse(
        String companyCode,
        String companyName,
        String ceoName,
        String businessType,
        String ceoPhoneNumber,
        String applicantName,
        String applicantPhoneNumber,
        String applicantEmail

) {
    public static UserResponse of(User user) {
        return UserResponse.builder()
                .companyCode(user.companyCode())
                .companyName(user.companyName())
                .ceoName(user.name())
                .businessType(determineBusinessType(user.companyCode()))
                .ceoPhoneNumber(user.phoneNumber())
                .applicantName(user.applicantName())
                .applicantPhoneNumber(user.applicantPhoneNumber())
                .applicantEmail(user.applicantEmail())
                .build();
    }

    private static String determineBusinessType(String companyCode) {
        String cleanedCompanyCode = companyCode.replaceAll("-", "");

        if (cleanedCompanyCode.length() != 10) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "사업자 등록번호 형식이 올바르지 않습니다.");
        }
        String middleTwoDigits = cleanedCompanyCode.substring(3, 5);

        int digits = Integer.parseInt(middleTwoDigits);
        String result = switch (digits) {
            case 81, 82, 83, 84, 85, 86, 87, 88 -> "법인";
            default -> "개인";
        };
        return result;

    }
}
