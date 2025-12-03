package com.nexsol.tpa.core.api.contorller.v1;

import com.nexsol.tpa.core.api.contorller.v1.request.SignInRequest;
import com.nexsol.tpa.core.domain.AuthService;
import com.nexsol.tpa.core.domain.AuthToken;
import com.nexsol.tpa.core.domain.EmailVerifiedService;
import com.nexsol.tpa.test.api.RestDocsTest; // 부모 클래스 import
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import tools.jackson.databind.ObjectMapper;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends RestDocsTest {

	private final AuthService authService = mock(AuthService.class);

	private final EmailVerifiedService emailVerifiedService = mock(EmailVerifiedService.class);

	private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기

	@Override
	protected Object initController() {
		return new AuthController(authService, emailVerifiedService);
	}

	@Test
	@DisplayName("로그인 API 문서화")
	public void login() throws Exception {
		// given
		AuthToken mockToken = AuthToken.builder()
			.accessToken("access-token-sample")
			.refreshToken("refresh-token-sample")
			.accessTokenExpiration(3600)
			.refreshTokenExpiration(1209600)
			.build();

		when(authService.signIn(any(), any(), any())).thenReturn(mockToken);

		SignInRequest request = new SignInRequest("123-45-67890", "test@nexsol.com", "123456");

		// when & then (Standard MockMvc Style)
		mockMvc
			.perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))) // 객체 -> JSON 문자열 변환
			.andExpect(status().isOk())
			.andDo(document("auth-login", requestPreprocessor(), responsePreprocessor(),
					requestFields(fieldWithPath("companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
							fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 주소"),
							fieldWithPath("code").type(JsonFieldType.STRING).description("인증 코드")),
					responseCookies(cookieWithName("refreshToken").description("리프레시 토큰 (HttpOnly, Secure)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"),
							fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
							fieldWithPath("data.expiresIn").type(JsonFieldType.NUMBER).description("만료 시간"))));
	}

}