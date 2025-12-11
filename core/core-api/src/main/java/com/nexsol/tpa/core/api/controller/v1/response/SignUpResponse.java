package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.User;

public record SignUpResponse(Long id, String companyCode, String email, String name) {
	public static SignUpResponse of(User user) {
		return new SignUpResponse(user.id(), user.companyCode(), user.applicantEmail(), user.name());
	}
}