package com.nexsol.tpa.core.api.contorller.v1.request;

import com.nexsol.tpa.core.domain.NewUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(@NotBlank String companyCode, @Email @NotBlank String email, String companyName,
		@NotBlank String name, String phoneNumber, String applicantName, @Email String applicantEmail,
		String applicantPhoneNumber, @NotNull Boolean termsAgreed, @NotNull Boolean privacyAgreed) {

	public NewUser toNewUser() {
		return NewUser.builder()
			.companyCode(companyCode)
			.email(email)
			.companyName(companyName)
			.name(name)
			.phoneNumber(phoneNumber)
			.applicantName(applicantName)
			.applicantEmail(applicantEmail)
			.applicantPhoneNumber(applicantPhoneNumber)
			.termsAgreed(termsAgreed)
			.privacyAgreed(privacyAgreed)
			.build();
	}
}
