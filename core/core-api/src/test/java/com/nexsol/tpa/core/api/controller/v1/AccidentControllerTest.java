package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.AccidentAttachmentRequest;
import com.nexsol.tpa.core.api.controller.v1.request.AccidentInfoRequest;
import com.nexsol.tpa.core.api.controller.v1.request.AccidentReportRequest;
import com.nexsol.tpa.core.api.controller.v1.request.DocumentRequest;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.AccidentStatus;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class AccidentControllerTest extends RestDocsTest {

	private final AccidentReportService accidentReportService = mock(AccidentReportService.class);

	private final FileService fileService = mock(FileService.class);

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

		this.webTestClient = MockMvcWebTestClient
			.bindToController(new AccidentController(accidentReportService, fileService))
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

	@Test
	@DisplayName("사고 접수 내역 리스트 조회")
	void getMyList() {
		// given
		Long userId = 1L;

		AccidentReport report = AccidentReport.builder()
			.id(100L)
			.accidentNumber("ACT-20251216-001")
			.insuredName("test")
			.plantName("해운대 햇살 발전소")
			.accidentInfo(AccidentInfo.builder()
				.accidentPlace("부산")
				.accidentType("재산")
				.accidentDate(LocalDateTime.now())
				.build())
			.reportedAt(LocalDateTime.now())
			.status(AccidentStatus.RECEIVED)
			.build();

		// Service가 PageResult<AccidentReport>를 반환하도록 Mocking
		PageResult<AccidentReport> result = new PageResult<>(List.of(report), 1, 1, 0, false);

		given(accidentReportService.getList(eq(userId), any(SortPage.class))).willReturn(result);

		// when & then
		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/accident/me")
				.queryParam("page", "0")
				.queryParam("size", "10")
				.queryParam("sort", "reportedAt")
				.queryParam("direction", "DESC")
				.build())
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("accident-list", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token")),
					queryParameters(parameterWithName("page").description("페이지 번호 (0부터 시작)"),
							parameterWithName("size").description("페이지 크기 (기본 10)"),
							parameterWithName("sort").description("정렬 필드"),
							parameterWithName("direction").description("정렬 방향")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
							fieldWithPath("data.content[].reportId").type(JsonFieldType.NUMBER).description("사고접수 ID"),
							fieldWithPath("data.content[].accidentNumber").type(JsonFieldType.STRING)
								.description("사고접수번호"),
							fieldWithPath("data.content[].insuredName").type(JsonFieldType.STRING).description("보험자명"),
							fieldWithPath("data.content[].plantName").type(JsonFieldType.STRING).description("발전소명"),
							fieldWithPath("data.content[].accidentPlace").type(JsonFieldType.STRING)
								.description("사고장소"),
							fieldWithPath("data.content[].accidentType").type(JsonFieldType.STRING).description("사고종류"),
							fieldWithPath("data.content[].accidentDate").type(JsonFieldType.STRING)
								.description("사고발생일시"),
							fieldWithPath("data.content[].reportedAt").type(JsonFieldType.STRING).description("접수일시"),
							fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("진행상태"),

							fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 수"),
							fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
							fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
							fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

	@Test
	@DisplayName("사고 접수 상세 조회")
	void getDetail() {
		// given
		Long userId = 1L;
		Long reportId = 100L;

		// Mocking Data
		AccidentReportDetail detail = AccidentReportDetail.builder()
			.id(reportId)
			.accidentNumber("ACT-20251216-001")
			.userId(userId)
			.status(AccidentStatus.RECEIVED)
			.reportedAt(LocalDateTime.now())
			// 개념 객체 1: 피보험자
			.insuredInfo(AccidentInsured.builder()
				.companyCode("123-45-67890")
				.ceoName("홍길동")
				.ceoPhone("010-1234-5678")
				.build())
			// 개념 객체 2: 발전소
			.plantInfo(AccidentPlant.builder()
				.name("해운대 햇살 발전소")
				.address("부산광역시 해운대구")
				.capacity(new BigDecimal("500.5"))
				.area(new BigDecimal("1000.0"))
				.build())
			// 개념 객체 3: 사고 상세
			.accidentInfo(AccidentInfo.builder()
				.accidentType("재산")
				.accidentDate(LocalDateTime.now())
				.accidentPlace("발전소 내부")
				.damageDescription("패널 파손")
				.estimatedLossAmount(1000000L)
				.accountBank("국민은행")
				.accountNumber("1234-5678")
				.accountHolder("홍길동")
				.build())
			// 첨부파일
			.attachments(List.of())
			.build();

		given(accidentReportService.getDetail(userId, reportId)).willReturn(detail);
		given(fileService.generatePresignedUrl(anyString())).willReturn("http://minio/url");

		// when & then
		webTestClient.get()
			.uri("/v1/accident/{reportId}", reportId)
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("accident-detail", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token")),
					pathParameters(parameterWithName("reportId").description("사고접수 ID")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),

							// 기본 정보
							fieldWithPath("data.reportId").type(JsonFieldType.NUMBER).description("사고접수 ID"),
							fieldWithPath("data.accidentNumber").type(JsonFieldType.STRING).description("사고접수번호"),
							fieldWithPath("data.status").type(JsonFieldType.STRING).description("진행상태"),
							fieldWithPath("data.reportedAt").type(JsonFieldType.STRING).description("접수일시"),

							// 1. 피보험자 정보 그룹
							fieldWithPath("data.insuredInfo").type(JsonFieldType.OBJECT).description("피보험자 정보"),
							fieldWithPath("data.insuredInfo.companyCode").type(JsonFieldType.STRING)
								.description("사업자등록번호"),
							fieldWithPath("data.insuredInfo.ceoName").type(JsonFieldType.STRING).description("대표자명"),
							fieldWithPath("data.insuredInfo.ceoPhone").type(JsonFieldType.STRING)
								.description("대표자 연락처"),

							// 2. 발전소 정보 그룹
							fieldWithPath("data.plantInfo").type(JsonFieldType.OBJECT).description("발전소 정보"),
							fieldWithPath("data.plantInfo.plantName").type(JsonFieldType.STRING).description("발전소명"),
							fieldWithPath("data.plantInfo.plantAddress").type(JsonFieldType.STRING)
								.description("발전소 주소"),
							fieldWithPath("data.plantInfo.capacity").type(JsonFieldType.NUMBER)
								.description("설비 용량 (kW)"),
							fieldWithPath("data.plantInfo.area").type(JsonFieldType.NUMBER).description("면적 (m2)"),

							// 3. 사고 상세 그룹
							fieldWithPath("data.accidentInfo").type(JsonFieldType.OBJECT).description("사고 상세 정보"),
							fieldWithPath("data.accidentInfo.accidentType").type(JsonFieldType.STRING)
								.description("사고 종류"),
							fieldWithPath("data.accidentInfo.accidentDate").type(JsonFieldType.STRING)
								.description("사고 일시"),
							fieldWithPath("data.accidentInfo.accidentPlace").type(JsonFieldType.STRING)
								.description("사고 장소"),
							fieldWithPath("data.accidentInfo.damageDescription").type(JsonFieldType.STRING)
								.description("피해 내용"),
							fieldWithPath("data.accidentInfo.estimatedLossAmount").type(JsonFieldType.NUMBER)
								.description("추정 손해액"),
							fieldWithPath("data.accidentInfo.accountBank").type(JsonFieldType.STRING)
								.description("입금 은행"),
							fieldWithPath("data.accidentInfo.accountNumber").type(JsonFieldType.STRING)
								.description("계좌 번호"),
							fieldWithPath("data.accidentInfo.accountHolder").type(JsonFieldType.STRING)
								.description("예금주")
								.optional(),

							// 4. 첨부파일 그룹
							fieldWithPath("data.attachments").type(JsonFieldType.ARRAY)
								.description("제출 서류 목록")
								.optional(),

							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

}