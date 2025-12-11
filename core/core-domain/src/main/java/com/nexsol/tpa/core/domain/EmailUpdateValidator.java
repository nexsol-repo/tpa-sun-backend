package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EmailUpdateValidator {

	private final EmailVerificationReader emailVerificationReader;

	public void validate(User user, ModifyUser modifyUser) {

		if (isEmailChanged(user, modifyUser)) {

			emailVerificationReader.read(user.companyCode(), modifyUser.applicantEmail(), EmailVerifiedType.UPDATE)
				.validateUpdate(LocalDateTime.now());
		}
	}

	private boolean isEmailChanged(User user, ModifyUser modifyUser) {
		return modifyUser.applicantEmail() != null
				&& !Objects.equals(user.applicantEmail(), modifyUser.applicantEmail());
	}

}
