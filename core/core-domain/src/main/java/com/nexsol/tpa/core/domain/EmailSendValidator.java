package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class EmailSendValidator {

	private final UserReader userReader;

	private final Map<EmailVerifiedType, Consumer<Boolean>> validationStrategies = Map.of(EmailVerifiedType.SIGNUP,
			this::validateSignup, EmailVerifiedType.SIGNIN, this::validateSignin, EmailVerifiedType.UPDATE,
			this::validateUpdate);

	public void validate(String companyCode, String email, EmailVerifiedType type) {
		boolean exists = userReader.exists(companyCode, email);

		// 타입에 맞는 검증 전략 실행 (전략이 없으면 통과하거나 에러 처리)
		validationStrategies.getOrDefault(type, _ -> {
		}).accept(exists);
	}

	private void validateSignup(boolean exists) {
		if (exists) {
			throw new CoreException(CoreErrorType.USER_EXIST_DATA);
		}
	}

	private void validateSignin(boolean exists) {
		if (!exists) {
			throw new CoreException(CoreErrorType.USER_NOT_FOUND);
		}
	}

	private void validateUpdate(boolean exists) {
		if (exists) {
			// "이미 사용 중인 이메일입니다" 라는 의미로 동일한 에러 혹은 별도 에러 사용
			throw new CoreException(CoreErrorType.USER_EXIST_DATA);
		}
	}

}
