package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenReader {

	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshToken read(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new CoreException(CoreErrorType.TOKEN_NOT_FOUND_DATA));

		if (refreshToken.isExpired()) {
			throw new CoreException(CoreErrorType.AUTH_UNAUTHORIZED);
		}
		return refreshToken;
	}

}
