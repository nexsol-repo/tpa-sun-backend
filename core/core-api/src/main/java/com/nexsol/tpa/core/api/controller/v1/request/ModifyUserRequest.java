package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.ModifyUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record ModifyUserRequest(
        String applicantName, @Email(message = "올바른 이메일 형식이 아닙니다.") String applicantEmail, @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.") String applicantPhoneNumber
) {

    public static ModifyUser toModifyUser(ModifyUserRequest request) {
        return ModifyUser.builder()
                .applicantName(request.applicantName())
                .applicantEmail(request.applicantEmail())
                .applicantPhoneNumber(request.applicantPhoneNumber())
                .build();
    }

}
