package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.EmailSendRequest;
import com.nexsol.tpa.core.api.controller.v1.request.EmailVerifyRequest;
import com.nexsol.tpa.core.api.controller.v1.request.SignInRequest;
import com.nexsol.tpa.core.domain.AuthService;
import com.nexsol.tpa.core.domain.AuthToken;
import com.nexsol.tpa.core.domain.EmailVerifiedService;
import com.nexsol.tpa.core.enums.EmailVerifiedType;

import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class AuthControllerTest extends RestDocsTest {

	private final AuthService authService = mock(AuthService.class);

	private final EmailVerifiedService emailVerifiedService = mock(EmailVerifiedService.class);

	@BeforeEach
	void setUp() {
		this.webTestClient = mockController(new AuthController(authService, emailVerifiedService));
	}

	@Test
	@DisplayName("로그인 API 문서화")
	public void login() {
		String companyCode = "123-45-67890";
		String email = "test@nexsol.com";
		String code = "123456";

		// given
		AuthToken mockToken = AuthToken.builder()
			.accessToken("access-token-sample")
			.refreshToken("refresh-token-sample")
			.accessTokenExpiration(3600)
			.refreshTokenExpiration(1209600)
			.build();

		given(authService.signIn(eq(companyCode), eq(email), eq(code)))
				.willReturn(mockToken);

		SignInRequest request = new SignInRequest("123-45-67890", "test@nexsol.com", "123456");

		// when & then (WebTestClient Style)
		webTestClient.post()
			.uri("/v1/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			// [변경] 객체를 바로 Body로 설정 (자동 JSON 변환)
			.bodyValue(request)
			.exchange() // 요청 실행

			// [검증]
			.expectStatus()
			.isOk()
			.expectCookie()
			.exists("refreshToken") // 쿠키 존재 여부 검증 (선택 사항)

			// [문서화]
			.expectBody()
			.consumeWith(document("auth-login", requestPreprocessor(), responsePreprocessor(),
					requestFields(fieldWithPath("companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
							fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 주소"),
							fieldWithPath("code").type(JsonFieldType.STRING).description("인증 코드")),
					responseCookies(cookieWithName("refreshToken").description("리프레시 토큰 (HttpOnly, Secure)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"),
							fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
							fieldWithPath("data.expiresIn").type(JsonFieldType.NUMBER).description("만료 시간"))));
	}

	@Test
	@DisplayName("이메일 인증코드 발송 API 문서화")
	void emailSend() {
		// given
		EmailSendRequest request = new EmailSendRequest("111-11-1111", "test@nexsol.com", EmailVerifiedType.SIGNUP);

		// void 메서드이므로 doNothing 사용 (또는 기본적으로 아무일도 안함)
		doNothing().when(emailVerifiedService).sendCode(any(), any(), any());

		// when & then
		webTestClient.post()
			.uri("/v1/auth/email/send")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("auth-email-send", requestPreprocessor(), responsePreprocessor(),
					requestFields(fieldWithPath("companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
							fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 주소"),
							fieldWithPath("type").type(JsonFieldType.STRING).description("인증 타입 (SIGNUP, SIGNIN)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

	@Test
	@DisplayName("이메일 인증코드 검증 API 문서화")
	void emailVerify() {
		// given
		EmailVerifyRequest request = new EmailVerifyRequest("test@nexsol.com", "123456", EmailVerifiedType.SIGNUP);

		doNothing().when(emailVerifiedService).verifyCode(any(), any(), any());

		// when & then
		webTestClient.post()
			.uri("/v1/auth/email/verify")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("auth-email-verify", requestPreprocessor(), responsePreprocessor(),
					requestFields(fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 주소"),
							fieldWithPath("code").type(JsonFieldType.STRING).description("인증 코드"),
							fieldWithPath("type").type(JsonFieldType.STRING).description("인증 타입 (SIGNUP, SIGNIN)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

}