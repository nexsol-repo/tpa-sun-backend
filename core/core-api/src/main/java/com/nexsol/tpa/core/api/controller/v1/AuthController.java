package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.EmailSendRequest;
import com.nexsol.tpa.core.api.controller.v1.request.EmailVerifyRequest;
import com.nexsol.tpa.core.api.controller.v1.request.SignInRequest;
import com.nexsol.tpa.core.api.controller.v1.response.AuthResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.domain.AuthService;
import com.nexsol.tpa.core.domain.AuthToken;
import com.nexsol.tpa.core.domain.EmailVerifiedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	private final EmailVerifiedService emailVerifiedService;

	@PostMapping(value = "/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid SignInRequest request) {
		AuthToken token = authService.signIn(request.companyCode(), request.email(), request.code());

		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.refreshToken())
			.httpOnly(true)
			// .secure(true)
			.secure(false)
			.path("/")
			.maxAge(token.refreshTokenExpiration())
			.sameSite("Strict")
			.build();
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
			.body(ApiResponse.success(AuthResponse.of(token)));
	}

	@PostMapping("/email/send")
	public ApiResponse<Object> sendEmailCode(@RequestBody @Valid EmailSendRequest request) {
		emailVerifiedService.sendCode(request.companyCode(), request.email(), request.type());
		return ApiResponse.success();
	}

	// 3. 이메일 인증번호 검증 (확인)
	@PostMapping("/email/verify")
	public ApiResponse<Object> verifyEmailCode(@RequestBody @Valid EmailVerifyRequest request) {
		emailVerifiedService.verifyCode(request.companyCode(), request.email(), request.code(), request.type());
		return ApiResponse.success();
	}

}
