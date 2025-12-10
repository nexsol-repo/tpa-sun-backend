package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record Applicant(String companyCode, String companyName, String ceoName, String ceoPhoneNumber,
		String applicantName, String applicantPhoneNumber, String applicantEmail) {
	public static Applicant toApplicant(User user) {
		return Applicant.builder()
			.companyCode(user.companyCode())
			.companyName(user.companyName())
			.ceoName(user.name())
			.ceoPhoneNumber(user.phoneNumber())
			.applicantName(user.applicantName())
			.applicantPhoneNumber(user.applicantPhoneNumber())
			.applicantEmail(user.applicantEmail())
			.build();
	}
}
