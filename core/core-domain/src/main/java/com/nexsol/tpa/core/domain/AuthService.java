package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserReader userReader;

	private final TokenIssuer tokenIssuer;

	private final TokenReader tokenReader;

	private final TokenRemover tokenRemover;

	private final TokenAppender tokenAppender;

	private final EmailVerificationReader emailVerificationReader;

	public AuthToken signIn(String companyCode, String email, String code) {
		User user = userReader.read(companyCode, email);

		EmailVerification verification = emailVerificationReader.read(companyCode,email, EmailVerifiedType.SIGNIN);

		verification.checkCodeForLogin(code, LocalDateTime.now());

		AuthToken authToken = tokenIssuer.issue(user.id(), user.applicantEmail());

		tokenAppender
			.append(RefreshToken.create(user.id(), authToken.refreshToken(), authToken.refreshTokenExpiration()));

		return authToken;
	}

	public AuthToken reissue(String currentRefreshToken) {

		RefreshToken storedToken = tokenReader.read(currentRefreshToken);

		User user = userReader.read(storedToken.userId());

		AuthToken newAuthToken = tokenIssuer.issue(user.id(), user.applicantEmail());

		tokenRemover.remove(currentRefreshToken);

		tokenAppender
			.append(RefreshToken.create(user.id(), newAuthToken.refreshToken(), newAuthToken.refreshTokenExpiration()));

		return newAuthToken;
	}

}
