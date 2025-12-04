package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.AuthToken;

public record AuthResponse(String accessToken, long expiresIn) {
	public static AuthResponse of(AuthToken token) {
		return new AuthResponse(token.accessToken(), token.accessTokenExpiration());
	}
}