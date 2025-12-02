package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserReader userReader;

	private final UserAppender userAppender;

	private final EmailVerificationReader emailVerificationReader;

	public User signUp(NewUser newUser) {
		EmailVerification verification = emailVerificationReader.read(newUser.email(), EmailVerifiedType.SIGNUP);

		verification.validateSignup(LocalDateTime.now());

		userReader.exist(newUser.companyCode(), newUser.email());

		return userAppender.append(newUser.toUser());
	}

	public User update(User user, ModifyUser modifyUser) {
		User updatedUser = user.update(modifyUser);

		return userAppender.append(updatedUser);
	}

}
