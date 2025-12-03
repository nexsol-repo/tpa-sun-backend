package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record User(Long id, String companyCode, String email, String companyName, String name, String phoneNumber,
		String applicantName, String applicantEmail, String applicantPhoneNumber) {

	public User update(ModifyUser modify) {
		return User.builder()
			.id(this.id)
			.companyCode(this.companyCode)
			.companyName(this.companyName)
			.name(this.name)
			.phoneNumber(this.phoneNumber)
			.email(modify.email())
			.applicantName(modify.applicantName())
			.applicantEmail(modify.applicantEmail())
			.applicantPhoneNumber(modify.applicantPhoneNumber())
			.build();
	}
}
