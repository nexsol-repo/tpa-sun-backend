package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.SignUpRequest;
import com.nexsol.tpa.core.domain.User;
import com.nexsol.tpa.core.domain.UserService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class UserControllerTest extends RestDocsTest {

	private final UserService userService = mock(UserService.class);

	@BeforeEach
	void setUp() {
		this.webTestClient = mockController(new UserController(userService));
	}

	@Test
	@DisplayName("회원가입 API 문서화")
	void signup() {
		// given
		SignUpRequest request = new SignUpRequest("123-45-67890", "test@nexsol.com", "(주)넥솔", "홍길동", "010-1234-5678",
				"신청자명", "applicant@nexsol.com", "010-2222-2222", true, true);

		User mockUser = User.builder().id(1L).companyCode("123-45-67890").email("test@nexsol.com").name("홍길동").build();

		when(userService.signUp(any())).thenReturn(mockUser);

		// when & then
		webTestClient.post()
			.uri("/v1/user/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("user-signup", requestPreprocessor(), responsePreprocessor(), requestFields(
					fieldWithPath("companyCode").type(JsonFieldType.STRING).description("사업자 번호 (필수)"),
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 (필수)"),
					fieldWithPath("companyName").type(JsonFieldType.STRING).description("회사명").optional(),
					fieldWithPath("name").type(JsonFieldType.STRING).description("대표자명 (필수)"),
					fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("연락처").optional(),
					fieldWithPath("applicantName").type(JsonFieldType.STRING).description("신청자명").optional(),
					fieldWithPath("applicantEmail").type(JsonFieldType.STRING).description("신청자 이메일").optional(),
					fieldWithPath("applicantPhoneNumber").type(JsonFieldType.STRING).description("신청자 연락처").optional(),
					fieldWithPath("termsAgreed").type(JsonFieldType.BOOLEAN).description("이용약관 동의 여부 (필수)"),
					fieldWithPath("privacyAgreed").type(JsonFieldType.BOOLEAN).description("개인정보 처리방침 동의 여부 (필수)")),
					responseFields(
							fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 유저 ID"),
							fieldWithPath("data.companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
							fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
							fieldWithPath("data.name").type(JsonFieldType.STRING).description("대표자명"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)"))));
	}

}
