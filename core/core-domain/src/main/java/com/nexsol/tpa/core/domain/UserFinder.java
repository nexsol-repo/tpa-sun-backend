package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinder {

	private final UserRepository userRepository;

	public User find(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new CoreException(CoreErrorType.USER_NOT_FOUND));

		return user;
	}

}
