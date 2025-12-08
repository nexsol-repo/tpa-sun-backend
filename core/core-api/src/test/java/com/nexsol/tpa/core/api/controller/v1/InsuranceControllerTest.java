package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.DocumentRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuranceConditionRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsurancePlantRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuranceStartRequest;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.BondSendStatus;
import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class InsuranceControllerTest extends RestDocsTest {

	private final InsuranceApplicationService insuranceApplicationService = mock(InsuranceApplicationService.class);

	@BeforeEach
	void setUp() {

		this.webTestClient = mockController(new InsuranceController(insuranceApplicationService));
	}

	private InsuranceApplication createMockApplication(Long id, InsuranceStatus status) {
		return InsuranceApplication.builder()
			.id(id)
			.applicationNumber("2025-APP-" + id)
			.userId(1L)
			.status(status)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();
	}

	@Test
	@DisplayName("청약서 상세 조회 (이어하기)")
	void getApplication() {
		// given
		Long appId = 1L;
		DocumentFile mockFile = DocumentFile.builder()
			.fileKey("insurance/2025/uuid.pdf")
			.originalFileName("doc.pdf")
			.size(1024L)
			.extension("pdf")
			.build();
		InsuranceAttachment att1 = InsuranceAttachment.builder()
			.type(InsuranceDocumentType.BUSINESS_LICENSE)
			.file(mockFile)
			.build();
		InsuranceDocument mockDocs = InsuranceDocument.builder().attachments(List.of(att1)).build();

		InsuranceApplication mockApp = InsuranceApplication.builder()
			.id(appId)
			.applicationNumber("2025-APP-" + appId)
			.status(InsuranceStatus.PENDING)
			.documents(mockDocs)
			.build();

		given(insuranceApplicationService.getInsuranceApplication(appId)).willReturn(mockApp);

		// when & then
		webTestClient.get()
			.uri("/v1/insurance/{applicationId}", appId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-get", requestPreprocessor(), responsePreprocessor(),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	@Test
	@DisplayName("Step 1. 청약 시작 (약관 동의)")
	void start() {
		// given
		InsuranceStartRequest request = new InsuranceStartRequest(true, true, true, true, true);
		InsuranceApplication mockApp = createMockApplication(101L, InsuranceStatus.PENDING);

		given(insuranceApplicationService.savePlantInit(any(), any(AgreementInfo.class))).willReturn(mockApp);

		// when & then
		webTestClient.post()
			.uri("/v1/insurance/start")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-start", requestPreprocessor(), responsePreprocessor(), requestFields(
					fieldWithPath("re100Interest").type(JsonFieldType.BOOLEAN)
						.description("RE100 발전함 장기고정계약 안내 관심 여부 (선택)"),
					fieldWithPath("personalInfoCollectionAgreed").type(JsonFieldType.BOOLEAN)
						.description("개인(신용)정보 수집 및 이용 동의 (필수)"),
					fieldWithPath("personalInfoThirdPartyAgreed").type(JsonFieldType.BOOLEAN)
						.description("개인정보 제3자 제공 동의 (필수)"),
					fieldWithPath("groupRuleAgreed").type(JsonFieldType.BOOLEAN)
						.description("단체규약 및 가입 시 유의사항 동의 (필수)"),
					fieldWithPath("marketingAgreed").type(JsonFieldType.BOOLEAN).description("마케팅 및 홍보서비스 동의 (선택)")),
					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	@Test
	@DisplayName("Step 2. 발전소 정보 저장 (임시저장)")
	void savePlant() {
		// given
		Long appId = 101L;
		InsurancePlantRequest request = new InsurancePlantRequest("해운대 햇살 발전소", "부산광역시 해운대구 우동 1234", "부산",
				new BigDecimal("500.0"), new BigDecimal("2000.0"), LocalDate.of(2023, 1, 1), "지붕위(판넬)", "고정식",
				"한국전력공사");

		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.PENDING);

		given(insuranceApplicationService.savePlantInfo(eq(appId), any(InsurancePlant.class))).willReturn(mockApp);

		// when & then
		webTestClient.post()
			.uri("/v1/insurance/{applicationId}/plant", appId)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-save-plant", requestPreprocessor(), responsePreprocessor(),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					requestFields(fieldWithPath("plantName").type(JsonFieldType.STRING).description("발전소명").optional(),
							fieldWithPath("address").type(JsonFieldType.STRING).description("주소").optional(),
							fieldWithPath("region").type(JsonFieldType.STRING).description("지역").optional(),
							fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("용량").optional(),
							fieldWithPath("area").type(JsonFieldType.NUMBER).description("면적").optional(),
							fieldWithPath("inspectionDate").type(JsonFieldType.STRING).description("검사일").optional(),
							fieldWithPath("facilityType").type(JsonFieldType.STRING).description("설비형태").optional(),
							fieldWithPath("driveMethod").type(JsonFieldType.STRING).description("구동방식").optional(),
							fieldWithPath("salesTarget").type(JsonFieldType.STRING).description("판매처").optional()),

					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	@Test
	@DisplayName("Step 3. 가입 조건 저장 (심사 및 보험료 산출)")
	void saveCondition() {
		// given
		Long appId = 101L;
		DocumentRequest mockDoc = new DocumentRequest("insurance/2025/uuid.pdf", "biz_license.pdf", 1024L, "pdf");
		InsuranceConditionRequest request = new InsuranceConditionRequest(true, // ESS
				300_000_000L, // 재물
				false, 100_000_000L, // 배상
				50_000_000L, // 휴지
				LocalDate.now().plusDays(1), // 개시일
				false, // 사고이력
				null, null, null, true, // 질권설정
				"국민은행", "홍길동", "010-1234-5678", 200_000_000L, "서울시...", BondSendStatus.NOT_SENT, "비고", mockDoc, // businessLicense
				mockDoc, // powerGenerationLicense
				null, // preUseInspection
				null, // supplyCertificate
				null // etc
		);

		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.PENDING);

		given(insuranceApplicationService.saveCondition(eq(appId), any(InsuranceCondition.class),
				any(InsuranceDocument.class)))
			.willReturn(mockApp);
		// when & then
		webTestClient.post()
			.uri("/v1/insurance/{applicationId}/condition", appId)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-save-condition", requestPreprocessor(), responsePreprocessor(),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					requestFields(
							fieldWithPath("essInstalled").type(JsonFieldType.BOOLEAN)
								.description("ESS 설치 여부")
								.optional(),
							fieldWithPath("propertyDamageAmount").type(JsonFieldType.NUMBER)
								.description("재물손해 가입금액")
								.optional(),
							fieldWithPath("civilWorkIncluded").type(JsonFieldType.BOOLEAN)
								.description("토목공사 포함 여부")
								.optional(),
							fieldWithPath("liabilityAmount").type(JsonFieldType.NUMBER)
								.description("배상책임 가입금액")
								.optional(),
							fieldWithPath("businessInterruptionAmount").type(JsonFieldType.NUMBER)
								.description("기업휴지 가입금액 (선택)")
								.optional(),
							fieldWithPath("startDate").type(JsonFieldType.STRING).description("보험 개시일").optional(),
							fieldWithPath("accidentHistory").type(JsonFieldType.BOOLEAN)
								.description("최근 5년 사고 이력 유무")
								.optional(),
							fieldWithPath("accidentDate").type(JsonFieldType.STRING)
								.description("사고 일자 (이력 있을 시)")
								.optional(),
							fieldWithPath("accidentPayment").type(JsonFieldType.NUMBER)
								.description("사고 보험금 (이력 있을 시)")
								.optional(),
							fieldWithPath("accidentContent").type(JsonFieldType.STRING)
								.description("사고 내용 (이력 있을 시)")
								.optional(),
							fieldWithPath("pledgeSet").type(JsonFieldType.BOOLEAN).description("질권 설정 유무").optional(),
							fieldWithPath("pledgeBankName").type(JsonFieldType.STRING).description("질권 은행명").optional(),
							fieldWithPath("pledgeManagerName").type(JsonFieldType.STRING)
								.description("질권 담당자명")
								.optional(),
							fieldWithPath("pledgeManagerPhone").type(JsonFieldType.STRING)
								.description("질권 담당자 연락처")
								.optional(),
							fieldWithPath("pledgeAmount").type(JsonFieldType.NUMBER).description("질권 설정 금액").optional(),
							fieldWithPath("pledgeAddress").type(JsonFieldType.STRING).description("질권 주소").optional(),
							fieldWithPath("pledgeBondStatus").type(JsonFieldType.STRING)
								.description("증권 송부 여부 (NOT_SENT, SENT, NOT_APPLICABLE)")
								.optional(),
							fieldWithPath("pledgeRemark").type(JsonFieldType.STRING).description("질권 비고").optional(),

							fieldWithPath("businessLicense.key").type(JsonFieldType.STRING)
								.description("사업자등록증 파일 키 (필수)")
								.optional(),
							fieldWithPath("businessLicense.name").type(JsonFieldType.STRING)
								.description("사업자등록증 원본명")
								.optional(),
							fieldWithPath("businessLicense.size").type(JsonFieldType.NUMBER)
								.description("사업자등록증 크기")
								.optional(),
							fieldWithPath("businessLicense.extension").type(JsonFieldType.STRING)
								.description("사업자등록증 확장자")
								.optional(),

							fieldWithPath("powerGenerationLicense.key").type(JsonFieldType.STRING)
								.description("발전사업허가증 파일 키 (필수)")
								.optional(),
							fieldWithPath("powerGenerationLicense.name").type(JsonFieldType.STRING)
								.description("발전사업허가증 원본명")
								.optional(),
							fieldWithPath("powerGenerationLicense.size").type(JsonFieldType.NUMBER)
								.description("발전사업허가증 크기")
								.optional(),
							fieldWithPath("powerGenerationLicense.extension").type(JsonFieldType.STRING)
								.description("발전사업허가증 확장자")
								.optional(),

							fieldWithPath("preUseInspection").type(JsonFieldType.OBJECT)
								.description("사용전 검사 확인증 (선택)")
								.optional(),
							fieldWithPath("supplyCertificate").type(JsonFieldType.OBJECT)
								.description("공급인증서 (선택)")
								.optional(),
							fieldWithPath("etc").type(JsonFieldType.OBJECT).description("기타 서류 (선택)").optional()

					),

					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	@Test
	@DisplayName("Step 4. 최종 가입 완료")
	void complete() {
		// given
		Long appId = 101L;
		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.COMPLETED);

		given(insuranceApplicationService.completeApplication(appId)).willReturn(mockApp);

		// when & then
		webTestClient.post()
			.uri("/v1/insurance/{applicationId}/complete", appId)
			.contentType(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-complete", requestPreprocessor(), responsePreprocessor(),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	private List<FieldDescriptor> getInsuranceResponseFields() {
		return List.of(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 코드"),
				fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("청약서 ID"),
				fieldWithPath("data.applicationNumber").type(JsonFieldType.STRING).description("청약 번호"),
				fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태"),

				subsectionWithPath("data.applicantInfo").type(JsonFieldType.OBJECT).optional().description("신청자 정보"),
				subsectionWithPath("data.agreementInfo").type(JsonFieldType.OBJECT).optional().description("동의 내역"),
				subsectionWithPath("data.plantInfo").type(JsonFieldType.OBJECT).optional().description("발전소 정보"),
				subsectionWithPath("data.conditionInfo").type(JsonFieldType.OBJECT).optional().description("가입 조건"),
				subsectionWithPath("data.coverageInfo").type(JsonFieldType.OBJECT).optional().description("보험료 정보"),

				// 문서 정보
				fieldWithPath("data.documentInfo").type(JsonFieldType.OBJECT).description("첨부 서류 정보").optional(),

				fieldWithPath("data.documentInfo.businessLicense").type(JsonFieldType.OBJECT)
					.description("사업자등록증")
					.optional(),
				fieldWithPath("data.documentInfo.businessLicense.key").type(JsonFieldType.STRING)
					.description("파일 키")
					.optional(),
				fieldWithPath("data.documentInfo.businessLicense.originalFileName").type(JsonFieldType.STRING)
					.description("파일명")
					.optional(), // 수정됨
				fieldWithPath("data.documentInfo.businessLicense.size").type(JsonFieldType.NUMBER)
					.description("크기")
					.optional(),
				fieldWithPath("data.documentInfo.businessLicense.extension").type(JsonFieldType.STRING)
					.description("확장자")
					.optional(),

				fieldWithPath("data.documentInfo.powerGenerationLicense").type(JsonFieldType.OBJECT)
					.description("발전사업허가증")
					.optional(),
				fieldWithPath("data.documentInfo.powerGenerationLicense.key").type(JsonFieldType.STRING)
					.description("파일 키")
					.optional(),
				fieldWithPath("data.documentInfo.powerGenerationLicense.originalFileName").type(JsonFieldType.STRING)
					.description("파일명")
					.optional(), // 수정됨
				fieldWithPath("data.documentInfo.powerGenerationLicense.size").type(JsonFieldType.NUMBER)
					.description("크기")
					.optional(),
				fieldWithPath("data.documentInfo.powerGenerationLicense.extension").type(JsonFieldType.STRING)
					.description("확장자")
					.optional(),

				// 나머지 서류들 (필요 시 subsectionWithPath로 축약 가능)
				subsectionWithPath("data.documentInfo.preUseInspection").type(JsonFieldType.OBJECT)
					.description("사용전 검사 확인증")
					.optional(),
				subsectionWithPath("data.documentInfo.supplyCertificate").type(JsonFieldType.OBJECT)
					.description("공급인증서")
					.optional(),
				subsectionWithPath("data.documentInfo.etc").type(JsonFieldType.OBJECT).description("기타 서류").optional(),

				fieldWithPath("error").type(JsonFieldType.STRING).optional().description("에러"));
	}

}
