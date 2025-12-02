package com.nexsol.tpa.core.api.contorller.v1;

import com.nexsol.tpa.core.api.contorller.v1.request.SignUpRequest;
import com.nexsol.tpa.core.api.contorller.v1.response.UserResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.domain.User;
import com.nexsol.tpa.core.domain.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ApiResponse<UserResponse> signup(@RequestBody @Valid SignUpRequest request) {

		User savedUser = userService.signUp(request.toNewUser());

		return ApiResponse.success(UserResponse.of(savedUser));
	}

}