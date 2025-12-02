package com.nexsol.tpa.core.domain;

public class NewUserBuilder {
    private String companyCode = "123-45-67890";
    private String email = "test@nexsol.com";
    private String companyName = "(주)넥솔";
    private String name = "테스트마스터";
    private String phoneNumber = "010-1234-5678";
    private String applicantName = "테스트신청자";
    private String applicantEmail = "applicant@nexsol.com";
    private String applicantPhoneNumber = "010-2222-2222";
    private boolean termsAgreed = true;
    private boolean privacyAgreed = true;


    public static NewUserBuilder aNewUser() {
        return new NewUserBuilder();
    }


    public NewUserBuilder withCompanyCode(String companyCode) {
        this.companyCode = companyCode;
        return this;
    }

    public NewUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public NewUserBuilder withTerms(boolean termsAgreed, boolean privacyAgreed) {
        this.termsAgreed = termsAgreed;
        this.privacyAgreed = privacyAgreed;
        return this;
    }


    public NewUser build() {
        return new NewUser(
                companyCode, email, companyName, name, phoneNumber,
                applicantName, applicantEmail, applicantPhoneNumber,
                termsAgreed, privacyAgreed
        );
    }
}