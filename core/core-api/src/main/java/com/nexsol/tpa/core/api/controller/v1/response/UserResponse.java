package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.User;

public record UserResponse(Long id, String companyCode, String email, String name) {
	public static UserResponse of(User user) {
		return new UserResponse(user.id(), user.companyCode(), user.applicantEmail(), user.name());
	}
}