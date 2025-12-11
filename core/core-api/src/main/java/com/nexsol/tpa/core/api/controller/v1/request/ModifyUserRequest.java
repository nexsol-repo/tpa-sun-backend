package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.ModifyUser;

public record ModifyUserRequest(
        String applicantName, String applicantEmail, String applicantPhoneNumber
) {

    public static ModifyUser toModifyUser(ModifyUserRequest request) {
        return ModifyUser.builder()
                .applicantName(request.applicantName())
                .applicantEmail(request.applicantEmail())
                .applicantPhoneNumber(request.applicantPhoneNumber())
                .build();
    }

}
