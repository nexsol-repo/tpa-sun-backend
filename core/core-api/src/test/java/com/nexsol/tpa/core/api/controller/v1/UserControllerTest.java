package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.SignUpRequest;
import com.nexsol.tpa.core.domain.ModifyUser;
import com.nexsol.tpa.core.domain.User;
import com.nexsol.tpa.core.domain.UserService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class UserControllerTest extends RestDocsTest {

	private final UserService userService = mock(UserService.class);

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		// 1. @AuthenticationPrincipal 처리를 위한 커스텀 리졸버
		HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
			@Override
			public boolean supportsParameter(MethodParameter parameter) {
				return parameter.getParameterType().equals(Long.class)
						&& parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
			}

			@Override
			public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
				return 1L; // 테스트용 User ID
			}
		};

		// 2. MockController 직접 빌드하여 부모 클래스(RestDocsTest)의 webTestClient 필드에 할당
		this.webTestClient = MockMvcWebTestClient.bindToController(new UserController(userService))
			.customArgumentResolvers(authPrincipalResolver) // 리졸버 등록
			.configureClient()
			.filter(documentationConfiguration(restDocumentation)) // RestDocs 설정 적용
			.build();
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
					responseFields(getUserResponseFields())));
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
					responseFields(getUserResponseFields())));
	}

	@Test
	@DisplayName("회원 정보 수정 API 문서화")
	void update() {
		// given
		Long userId = 1L;

		ModifyUser request = ModifyUser.builder()
			.applicantName("변경된신청자")
			.applicantEmail("new@nexsol.com") // 이메일 변경 시도
			.applicantPhoneNumber("010-9999-9999")
			.build();

		User updatedUser = User.builder()
			.companyCode("123-45-67890")
			.companyName("(주)넥솔")
			.name("testMaster")
			.phoneNumber("010-1234-5678")
			.applicantName("변경된신청자")
			.applicantPhoneNumber("010-9999-9999")
			.applicantEmail("new@nexsol.com")
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
					responseFields(getUserResponseFields())));
	}

	private FieldDescriptor[] getUserResponseFields() {
		return new FieldDescriptor[] {
				fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
				fieldWithPath("data.companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
				fieldWithPath("data.companyName").type(JsonFieldType.STRING).description("회사명").optional(),
				fieldWithPath("data.ceoName").type(JsonFieldType.STRING).description("대표자명"),
				fieldWithPath("data.businessType").type(JsonFieldType.STRING).description("사업자 구분 (개인/법인)"),
				fieldWithPath("data.ceoPhoneNumber").type(JsonFieldType.STRING).description("대표자 연락처").optional(),
				fieldWithPath("data.applicantName").type(JsonFieldType.STRING).description("신청자명"),
				fieldWithPath("data.applicantPhoneNumber").type(JsonFieldType.STRING).description("신청자 연락처"),
				fieldWithPath("data.applicantEmail").type(JsonFieldType.STRING).description("신청자 이메일"),
				fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공 시 null)").optional() };
	}

}
