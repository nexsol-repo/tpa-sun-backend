package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.ApplicantInfo;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class ApplicantInfoEmbeddable {
    private String appCompanyCode;
    private String appCompanyName;
    private String appCeoName;
    private String appCeoPhone;
    private String appApplicantName;
    private String appApplicantPhone;
    private String appEmail;

    public ApplicantInfoEmbeddable(ApplicantInfo domain) {
        this.appCompanyCode = domain.companyCode();
        this.appCompanyName = domain.companyName();
        this.appCeoName = domain.ceoName();
        this.appCeoPhone = domain.ceoPhoneNumber();
        this.appApplicantName = domain.applicantName();
        this.appApplicantPhone = domain.applicantPhoneNumber();
        this.appEmail = domain.email();
    }

    public ApplicantInfo toDomain(){
        return ApplicantInfo.builder()
                .companyCode(appCompanyCode)
                .companyName(appCompanyName)
                .ceoName(appCeoName)
                .ceoPhoneNumber(appCeoPhone)
                .applicantName(appApplicantName)
                .applicantPhoneNumber(appApplicantPhone)
                .email(appEmail)
                .build();
    }
}
