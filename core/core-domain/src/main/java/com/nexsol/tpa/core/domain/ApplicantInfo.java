package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record ApplicantInfo(
        String companyCode,
        String companyName,
        String ceoName,
        String ceoPhoneNumber,
        String applicantName,
        String applicantPhoneNumber,
        String email

) {


}
