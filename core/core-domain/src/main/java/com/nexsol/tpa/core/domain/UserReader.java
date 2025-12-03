package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReader {

	private final UserRepository userRepository;

	public User read(String companyCode, String email) {
		return userRepository.findByCompanyCodeAndEmail(companyCode, email)
			.orElseThrow(() -> new CoreException(CoreErrorType.USER_NOT_FOUND));
	}

	public void exist(String bizNo, String email) {
		if (userRepository.existsCompanyCodeAndEmail(bizNo, email)) {

			throw new CoreException(CoreErrorType.USER_EXIST_DATA);
		}
	}

}
