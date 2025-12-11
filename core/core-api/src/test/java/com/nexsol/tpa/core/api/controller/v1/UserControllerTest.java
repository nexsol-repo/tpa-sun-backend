package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.SignUpRequest;
import com.nexsol.tpa.core.domain.ModifyUser;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
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
		SignUpRequest request = new SignUpRequest("123-45-67890", "(주)넥솔", "testMaster", "010-1234-5678", "testMaster",
				"applicant@nexsol.com", "010-1234-5678", true, true);

		User mockUser = User.builder()
			.companyCode("123-45-67890")
			.companyName("(주)넥솔")
			.name("testMaster")
			.phoneNumber("010-1234-5678")
			.applicantName("testMaster")
			.applicantPhoneNumber("010-1234-5678")
			.applicantEmail("applicant@nexsol.com")
			.build();

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
					fieldWithPath("companyName").type(JsonFieldType.STRING).description("회사명 (필수)").optional(),
					fieldWithPath("name").type(JsonFieldType.STRING).description("대표자명 (필수)"),
					fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("연락처 (필수)").optional(),
					fieldWithPath("applicantName").type(JsonFieldType.STRING).description("신청자명 (필수)").optional(),
					fieldWithPath("applicantEmail").type(JsonFieldType.STRING).description("신청자 이메일 (필수)").optional(),
					fieldWithPath("applicantPhoneNumber").type(JsonFieldType.STRING)
						.description("신청자 연락처 (필수)")
						.optional(),
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

	@Test
	@DisplayName("나의 정보 조회 API 문서화")
	void me() {
		// given
		Long userId = 1L;

		User mockUser = User.builder()
			.companyCode("123-45-67890")
			.companyName("(주)넥솔")
			.name("testMaster")
			.phoneNumber("010-1234-5678")
			.applicantName("testMaster")
			.applicantPhoneNumber("010-1234-5678")
			.applicantEmail("applicant@nexsol.com")
			.build();

		given(userService.findUser(userId)).willReturn(mockUser);

		// when & then
		webTestClient.get()
			.uri("/v1/user/me")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("user-me", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 ID"),
							fieldWithPath("data.companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
							fieldWithPath("data.companyName").type(JsonFieldType.STRING).description("회사명").optional(),
							fieldWithPath("data.name").type(JsonFieldType.STRING).description("대표자명"),
							fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

	@Test
	@DisplayName("회원 정보 수정 API 문서화")
	void update() {
		// given
		Long userId = 1L;

		// 요청 DTO (Controller에 별도 Request DTO가 있다면 그것을 사용, 여기선 편의상 ModifyUser 사용 가정)
		// 실제로는 UpdateUserRequest 같은 DTO를 만들어 @RequestBody로 받는 것이 일반적입니다.
		ModifyUser request = ModifyUser.builder()
			.applicantName("변경된신청자")
			.applicantEmail("new@nexsol.com") // 이메일 변경 시도
			.applicantPhoneNumber("010-9999-9999")
			.build();

		User updatedUser = User.builder()
			.id(userId)
			.companyCode("123-45-67890")
			.name("홍길동")
			.applicantName("변경된신청자")
			.applicantEmail("new@nexsol.com")
			.applicantPhoneNumber("010-9999-9999")
			.build();

		given(userService.update(eq(userId), any(ModifyUser.class))).willReturn(updatedUser);

		// when & then
		webTestClient.patch() // 보통 수정은 PATCH or PUT
			.uri("/v1/user/me")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("user-update", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token")),
					requestFields(
							fieldWithPath("applicantName").type(JsonFieldType.STRING)
								.description("변경할 신청자명")
								.optional(),
							fieldWithPath("applicantEmail").type(JsonFieldType.STRING)
								.description("변경할 이메일 (인증 필요)")
								.optional(),
							fieldWithPath("applicantPhoneNumber").type(JsonFieldType.STRING)
								.description("변경할 연락처")
								.optional()),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 ID"),
							fieldWithPath("data.email").type(JsonFieldType.STRING).description("변경된 이메일"),
							fieldWithPath("data.name").type(JsonFieldType.STRING).description("대표자명"),
							// 필요한 필드 추가
							fieldWithPath("data.companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
							fieldWithPath("data.companyName").type(JsonFieldType.STRING).description("회사명").optional(),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

}
