package com.nexsol.tpa.core.domain;

import lombok.Builder;


@Builder
public record User(Long id, String companyCode, String companyName, String name, String phoneNumber,
                   String applicantName, String applicantEmail, String applicantPhoneNumber) {

    public User update(ModifyUser modify) {
        return User.builder()
                .id(this.id)
                .companyCode(this.companyCode)
                .companyName(this.companyName)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .applicantName(modify.applicantName() != null ? modify.applicantName() : this.applicantName)
                .applicantEmail(modify.applicantEmail() != null ? modify.applicantEmail() : this.applicantEmail)
                .applicantPhoneNumber(modify.applicantPhoneNumber() != null ? modify.applicantPhoneNumber() : this.applicantPhoneNumber)
                .build();

    }


}
