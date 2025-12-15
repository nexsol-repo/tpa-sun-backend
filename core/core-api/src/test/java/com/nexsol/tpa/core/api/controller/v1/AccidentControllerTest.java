package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.AccidentAttachmentRequest;
import com.nexsol.tpa.core.api.controller.v1.request.AccidentInfoRequest;
import com.nexsol.tpa.core.api.controller.v1.request.AccidentReportRequest;
import com.nexsol.tpa.core.api.controller.v1.request.DocumentRequest;
import com.nexsol.tpa.core.domain.AccidentReport;
import com.nexsol.tpa.core.domain.AccidentReportService;
import com.nexsol.tpa.core.domain.NewAccidentReport;
import com.nexsol.tpa.core.enums.AccidentStatus;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class AccidentControllerTest extends RestDocsTest {

	private final AccidentReportService accidentReportService = mock(AccidentReportService.class);

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		// @AuthenticationPrincipal Mocking
		HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
			@Override
			public boolean supportsParameter(MethodParameter parameter) {
				return parameter.getParameterType().equals(Long.class)
						&& parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
			}

			@Override
			public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
				return 1L; // Test User ID
			}
		};

		this.webTestClient = MockMvcWebTestClient.bindToController(new AccidentController(accidentReportService))
			.customArgumentResolvers(authPrincipalResolver)
			.configureClient()
			.filter(documentationConfiguration(restDocumentation))
			.build();
	}

	@Test
	@DisplayName("신규 사고 접수 API 문서화")
	void reportAccident() {
		// given
		Long userId = 1L;
		Long applicationId = 100L;

		// 1. 요청 데이터 준비
		AccidentInfoRequest infoRequest = new AccidentInfoRequest("재산 손해", LocalDateTime.now(), "전라남도 나주시 빛가람동 123",
				"태풍으로 인한 패널 파손", 50_000_000L, "신한", "110-3333-3333", "테스터");

		DocumentRequest fileRequest = new DocumentRequest("accidents/2025/uuid.jpg", "scene.jpg", 1024L, "jpg");
		AccidentAttachmentRequest attachmentRequest = new AccidentAttachmentRequest("현장사진", fileRequest);

		AccidentReportRequest request = new AccidentReportRequest(applicationId, infoRequest,
				List.of(attachmentRequest));

		// 2. 응답 Mocking
		AccidentReport savedReport = AccidentReport.builder()
			.id(1L)
			.applicationId(applicationId)
			.status(AccidentStatus.RECEIVED)
			.reportedAt(LocalDateTime.now())
			.build();

		given(accidentReportService.reportAccident(any(NewAccidentReport.class))).willReturn(savedReport);

		// when & then
		webTestClient.post()
			.uri("/v1/accident/report")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("accident-report", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token")),
					requestFields(
							fieldWithPath("applicationId").type(JsonFieldType.NUMBER)
								.description("사고 접수 대상 계약(발전소) ID"),

							// 사고 상세 정보
							fieldWithPath("accidentInfo.accidentType").type(JsonFieldType.STRING)
								.description("사고 종류 (신체/재산/법적방어)"),
							fieldWithPath("accidentInfo.accidentDate").type(JsonFieldType.STRING)
								.description("사고 발생 일시"),
							fieldWithPath("accidentInfo.accidentPlace").type(JsonFieldType.STRING)
								.description("사고 발생 장소"),
							fieldWithPath("accidentInfo.damageDescription").type(JsonFieldType.STRING)
								.description("피해 내용 및 경위"),
							fieldWithPath("accidentInfo.estimatedLossAmount").type(JsonFieldType.NUMBER)
								.description("추정 손해액"),
							fieldWithPath("accidentInfo.accountBank").type(JsonFieldType.STRING).description("은행명"),
							fieldWithPath("accidentInfo.accountNumber").type(JsonFieldType.STRING).description("계좌번호"),
							fieldWithPath("accidentInfo.accountHolder").type(JsonFieldType.STRING)
								.description("예금주")
								.optional(),

							// 첨부 파일 리스트
							fieldWithPath("attachments[]").type(JsonFieldType.ARRAY).description("증빙 서류 목록").optional(),
							fieldWithPath("attachments[].type").type(JsonFieldType.STRING)
								.description("서류 구분 (현장사진, 견적서 등)"),
							fieldWithPath("attachments[].file.key").type(JsonFieldType.STRING)
								.description("업로드된 파일 Key"),
							fieldWithPath("attachments[].file.name").type(JsonFieldType.STRING).description("원본 파일명"),
							fieldWithPath("attachments[].file.size").type(JsonFieldType.NUMBER).description("파일 크기"),
							fieldWithPath("attachments[].file.extension").type(JsonFieldType.STRING)
								.description("파일 확장자")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data.reportId").type(JsonFieldType.NUMBER).description("생성된 사고 접수 ID"),
							fieldWithPath("data.applicationId").type(JsonFieldType.NUMBER).description("연결된 계약 ID"),
							fieldWithPath("data.status").type(JsonFieldType.STRING).description("접수 상태 (RECEIVED 등)"),
							fieldWithPath("data.reportedAt").type(JsonFieldType.STRING).description("접수 일시"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

}