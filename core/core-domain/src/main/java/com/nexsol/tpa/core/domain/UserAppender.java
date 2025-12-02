package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAppender {

	private final UserRepository userRepository;

	public User append(User user) {
		return userRepository.save(user);
	}

}
