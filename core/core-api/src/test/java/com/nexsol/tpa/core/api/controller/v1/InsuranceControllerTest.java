package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.InsuranceConditionRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsurancePlantRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuranceStartRequest;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.BondSendStatus;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.PENDING);

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
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 코드"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("청약서 ID"),
							fieldWithPath("data.applicationNumber").type(JsonFieldType.STRING).description("청약 번호"),
							fieldWithPath("data.status").type(JsonFieldType.STRING)
								.description("진행 상태 (WRITING, COMPLETED...)"),
							fieldWithPath("data.applicantInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("신청자 정보 스냅샷"),
							fieldWithPath("data.agreementInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("동의 내역"),
							fieldWithPath("data.plantInfo").type(JsonFieldType.OBJECT).optional().description("발전소 정보"),
							fieldWithPath("data.conditionInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("가입 조건"),
							fieldWithPath("data.coverageInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("보험료 산출 내역"),
							fieldWithPath("error").type(JsonFieldType.STRING)
								.optional()
								.description("에러 메시지 (정상 시 null)"))));
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
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 코드"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 청약서 ID"),
							fieldWithPath("data.applicationNumber").type(JsonFieldType.STRING).description("청약 번호"),
							fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태"),
							fieldWithPath("data.applicantInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("신청자 정보"),
							fieldWithPath("data.agreementInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("동의 내역"),
							fieldWithPath("data.plantInfo").type(JsonFieldType.OBJECT).optional().description("발전소 정보"),
							fieldWithPath("data.conditionInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("가입 조건"),
							fieldWithPath("data.coverageInfo").type(JsonFieldType.OBJECT)
								.optional()
								.description("보험료 정보"),
							fieldWithPath("error").type(JsonFieldType.STRING).optional().description("에러"))));
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
							fieldWithPath("address").type(JsonFieldType.STRING).description("발전소 기본 주소").optional(),
							fieldWithPath("addressDetail").type(JsonFieldType.STRING)
								.description("발전소 상세 주소")
								.optional(),
							fieldWithPath("region").type(JsonFieldType.STRING)
								.description("지역 (부산, 전남 등 - Daum API 반환값)")
								.optional(),
							fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("설비 용량 (kW)").optional(),
							fieldWithPath("area").type(JsonFieldType.NUMBER).description("설비 면적 (m²)").optional(),
							fieldWithPath("inspectionDate").type(JsonFieldType.STRING)
								.description("사용전 검사일")
								.optional(),
							fieldWithPath("facilityType").type(JsonFieldType.STRING)
								.description("설비 형태 (평지, 지붕위 등)")
								.optional(),
							fieldWithPath("driveMethod").type(JsonFieldType.STRING).description("구동 방식").optional(),
							fieldWithPath("salesTarget").type(JsonFieldType.STRING).description("전력 판매처").optional()),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 코드"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("청약서 ID"),
							fieldWithPath("data.applicationNumber").type(JsonFieldType.STRING).description("청약 번호"),
							fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태"),
							fieldWithPath("data.plantInfo").type(JsonFieldType.OBJECT)
								.description("업데이트된 발전소 정보")
								.optional(),
							fieldWithPath("data.applicantInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.agreementInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.conditionInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.coverageInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("error").type(JsonFieldType.STRING).optional().ignored())));
	}

	@Test
	@DisplayName("Step 3. 가입 조건 저장 (심사 및 보험료 산출)")
	void saveCondition() {
		// given
		Long appId = 101L;
		InsuranceConditionRequest request = new InsuranceConditionRequest(true, // ESS
				300_000_000L, // 재물
				false, 100_000_000L, // 배상
				50_000_000L, // 휴지
				LocalDate.now().plusDays(1), // 개시일
				false, // 사고이력
				null, null, null, true, // 질권설정
				"국민은행", "홍길동", "010-1234-5678", 200_000_000L, "서울시...", BondSendStatus.NOT_SENT, "비고");

		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.PENDING);

		given(insuranceApplicationService.saveCondition(eq(appId), any(InsuranceCondition.class))).willReturn(mockApp);

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
							fieldWithPath("pledgeRemark").type(JsonFieldType.STRING).description("질권 비고").optional()),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 코드"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("청약서 ID"),
							fieldWithPath("data.applicationNumber").type(JsonFieldType.STRING).description("청약 번호"),
							fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태"),
							fieldWithPath("data.conditionInfo").type(JsonFieldType.OBJECT)
								.description("업데이트된 가입 조건")
								.optional(),
							fieldWithPath("data.coverageInfo").type(JsonFieldType.OBJECT)
								.description("산출된 보험료 정보 (자동 계산)")
								.optional(),
							fieldWithPath("data.applicantInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.agreementInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.plantInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("error").type(JsonFieldType.STRING).optional().ignored())));
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
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 코드"),
							fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("청약서 ID"),
							fieldWithPath("data.applicationNumber").type(JsonFieldType.STRING).description("청약 번호"),
							fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태 (COMPLETED)"),
							fieldWithPath("data.applicantInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.agreementInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.plantInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.conditionInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("data.coverageInfo").type(JsonFieldType.OBJECT).optional().ignored(),
							fieldWithPath("error").type(JsonFieldType.STRING).optional().ignored())));
	}

}
