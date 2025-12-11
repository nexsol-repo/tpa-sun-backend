package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.*;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.BondSendStatus;
import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class InsuranceControllerTest extends RestDocsTest {

	private final InsuranceApplicationService insuranceApplicationService = mock(InsuranceApplicationService.class);

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
		this.webTestClient = MockMvcWebTestClient.bindToController(new InsuranceController(insuranceApplicationService))
			.customArgumentResolvers(authPrincipalResolver) // 리졸버 등록
			.configureClient()
			.filter(documentationConfiguration(restDocumentation)) // RestDocs 설정 적용
			.build();
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
	@DisplayName("마이페이지 보험 가입 내역 리스트 조회")
	void getMyList() {
		// given
		Long userId = 1L;

		// Mock Data 생성 (InsuranceListResponse 매핑을 위한 데이터 포함)
		InsuranceApplication app = InsuranceApplication.builder()
			.id(1L)
			.applicationNumber("2025-APP-001")
			.userId(userId)
			.status(InsuranceStatus.COMPLETED)
			.plant(InsurancePlant.builder().name("해운대 햇살 발전소").build()) // 발전소명
			.applicant(Applicant.builder().applicantName("홍길동").build()) // 신청자명
			.quote(PremiumQuote.builder().totalPremium(1250000L).build()) // 납입 보험료
			.condition(JoinCondition.builder().startDate(LocalDate.of(2025, 1, 1)).build()) // 보험
			// 시작일
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now()) // 결제일(예시)
			.build();

		List<InsuranceApplication> content = List.of(app);
		PageResult<InsuranceApplication> pageResult = new PageResult<>(content, 1L, 1, 0, false);

		// Service Mocking
		given(insuranceApplicationService.getList(eq(userId), any(SortPage.class))).willReturn(pageResult);

		// when & then
		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/v1/insurance/me")
				.queryParam("page", "0")
				.queryParam("size", "10")
				.queryParam("sort", "createdAt")
				.queryParam("direction", "DESC")
				.build())
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-my-list", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					queryParameters(parameterWithName("page").description("페이지 번호 (0부터 시작)"),
							parameterWithName("size").description("페이지 크기 (기본 10)"),
							parameterWithName("sort").description("정렬 필드").optional(),
							parameterWithName("direction").description("정렬 방향").optional()),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
							// PageResponse 필드
							fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("가입 내역 리스트"),
							fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 수"),
							fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
							fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
							fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),

							// InsuranceListResponse 필드 (content 배열 내부)
							fieldWithPath("data.content[].applicationId").type(JsonFieldType.NUMBER)
								.description("청약서 ID"),
							fieldWithPath("data.content[].applicationNumber").type(JsonFieldType.STRING)
								.description("청약 번호"),
							fieldWithPath("data.content[].plantName").type(JsonFieldType.STRING)
								.description("발전소명")
								.optional(),
							fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("진행 상태"),
							fieldWithPath("data.content[].applicantName").type(JsonFieldType.STRING)
								.description("신청자명")
								.optional(),
							fieldWithPath("data.content[].totalPremium").type(JsonFieldType.NUMBER)
								.description("납입 보험료")
								.optional(),
							fieldWithPath("data.content[].nextStep").type(JsonFieldType.NUMBER)
								.description("작성 스텝 단계")
								.optional(),

							fieldWithPath("data.content[].startDate").type(JsonFieldType.STRING)
								.description("보험 시작일")
								.optional(),
							fieldWithPath("data.content[].endDate").type(JsonFieldType.STRING)
								.description("보험 종료일 (시작일 + 1년)")
								.optional(),
							fieldWithPath("data.content[].paymentDate").type(JsonFieldType.STRING)
								.description("결제일 (가입완료 시)")
								.optional(),

							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

	@Test
	@DisplayName("청약서 상세 조회 (이어하기)")
	void getApplication() {
		// given
		Long appId = 1L;
		Long userId = 1L;
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

		given(insuranceApplicationService.getInsuranceApplication(userId, appId)).willReturn(mockApp);

		// when & then
		webTestClient.get()
			.uri("/v1/insurance/{applicationId}", appId)
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-get", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	@Test
	@DisplayName("Step 1. 청약 시작 (약관 동의)")
	void start() {
		Long userId = 1L;
		// given
		InsuranceStartRequest request = new InsuranceStartRequest(true, true, true, true, true);
		InsuranceApplication mockApp = createMockApplication(101L, InsuranceStatus.PENDING);

		given(insuranceApplicationService.saveInit(eq(userId), any(Agreement.class))).willReturn(mockApp);

		// when & then
		webTestClient.post()
			.uri("/v1/insurance/start")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-start", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					requestFields(
							fieldWithPath("re100Interest").type(JsonFieldType.BOOLEAN)
								.description("RE100 발전함 장기고정계약 안내 관심 여부 (선택)"),
							fieldWithPath("personalInfoCollectionAgreed").type(JsonFieldType.BOOLEAN)
								.description("개인(신용)정보 수집 및 이용 동의 (필수)"),
							fieldWithPath("personalInfoThirdPartyAgreed").type(JsonFieldType.BOOLEAN)
								.description("개인정보 제3자 제공 동의 (필수)"),
							fieldWithPath("groupRuleAgreed").type(JsonFieldType.BOOLEAN)
								.description("단체규약 및 가입 시 유의사항 동의 (필수)"),
							fieldWithPath("marketingAgreed").type(JsonFieldType.BOOLEAN)
								.description("마케팅 및 홍보서비스 동의 (선택)")),
					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	@Test
	@DisplayName("Step 2. 발전소 정보 저장 (임시저장)")
	void savePlant() {
		// given
		Long userId = 1L;
		Long appId = 101L;
		InsurancePlantRequest request = new InsurancePlantRequest("해운대 햇살 발전소", "부산광역시 해운대구 우동 1234", "부산",
				new BigDecimal("500.0"), new BigDecimal("2000.0"), LocalDate.of(2023, 1, 1), "지붕위(판넬)", "고정식",
				"한국전력공사");

		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.PENDING);

		given(insuranceApplicationService.savePlantInfo(eq(userId), eq(appId), any(InsurancePlant.class)))
			.willReturn(mockApp);

		// when & then
		webTestClient.post()
			.uri("/v1/insurance/{applicationId}/plant", appId)
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-save-plant", requestPreprocessor(), responsePreprocessor(),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
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
		Long userId = 1L;
		Long appId = 101L;

		// 1. 문서 요청 객체 생성 (DocumentRequest)
		DocumentRequest mockDoc = new DocumentRequest("insurance/2025/uuid.pdf", "biz_license.pdf", 1024L, "pdf");

		// 2. [Refactored] 계층형 문서 세트 생성
		DocumentSetRequest documentSet = new DocumentSetRequest(mockDoc, // businessLicense
				mockDoc, // powerGenerationLicense
				null, // preUseInspection
				null, // supplyCertificate
				null // etc
		);

		// 3. [Refactored] 질권 정보 객체 생성
		PledgeRequest pledgeRequest = new PledgeRequest("국민은행", "홍길동", "010-1234-5678", 200_000_000L, "서울시...",
				BondSendStatus.NOT_SENT, "비고");

		// 4. [Refactored] 메인 요청 객체 생성 (Accident는 null로 가정)
		InsuranceConditionRequest request = new InsuranceConditionRequest(true, // essInstalled
				300_000_000L, // propertyDamageAmount
				true, 100_000_000L, // liabilityAmount
				50_000_000L, // businessInterruptionAmount

				LocalDate.now().plusDays(1), // startDate
				null, // accident (없음)
				pledgeRequest, // pledge
				documentSet // documents
		);

		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.PENDING);

		// Service Mocking (도메인 객체 매핑 확인)
		given(insuranceApplicationService.saveCondition(eq(userId), eq(appId), any(JoinCondition.class),
				any(InsuranceDocument.class)))
			.willReturn(mockApp);

		// when & then
		webTestClient.post()
			.uri("/v1/insurance/{applicationId}/condition", appId)
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-save-condition", requestPreprocessor(), responsePreprocessor(),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					requestFields(
							// 1. 기본 정보
							fieldWithPath("essInstalled").type(JsonFieldType.BOOLEAN).description("ESS 설치 여부"),
							fieldWithPath("propertyDamageAmount").type(JsonFieldType.NUMBER)
								.description("재물손해 가입금액")
								.optional(),
							fieldWithPath("civilWorkIncluded").type(JsonFieldType.BOOLEAN).description("토목공사 여부"),

							fieldWithPath("liabilityAmount").type(JsonFieldType.NUMBER)
								.description("배상책임 가입금액")
								.optional(),
							fieldWithPath("businessInterruptionAmount").type(JsonFieldType.NUMBER)
								.description("기업휴지 가입금액")
								.optional(),
							fieldWithPath("startDate").type(JsonFieldType.STRING).description("보험 개시일").optional(),

							// 2. 사고 이력 (Optional Group)
							fieldWithPath("accident").type(JsonFieldType.OBJECT)
								.description("사고 이력 정보 (없으면 null)")
								.optional(),
							fieldWithPath("accident.date").type(JsonFieldType.STRING).description("사고 일자").optional(),
							fieldWithPath("accident.paymentAmount").type(JsonFieldType.NUMBER)
								.description("사고 보험금")
								.optional(),
							fieldWithPath("accident.content").type(JsonFieldType.STRING)
								.description("사고 내용")
								.optional(),

							// 3. 질권 설정 (Optional Group)
							fieldWithPath("pledge").type(JsonFieldType.OBJECT)
								.description("질권 설정 정보 (없으면 null)")
								.optional(),
							fieldWithPath("pledge.bankName").type(JsonFieldType.STRING)
								.description("질권 은행명")
								.optional(),
							fieldWithPath("pledge.managerName").type(JsonFieldType.STRING)
								.description("질권 담당자명")
								.optional(),
							fieldWithPath("pledge.managerPhone").type(JsonFieldType.STRING)
								.description("질권 담당자 연락처")
								.optional(),
							fieldWithPath("pledge.amount").type(JsonFieldType.NUMBER)
								.description("질권 설정 금액")
								.optional(),
							fieldWithPath("pledge.address").type(JsonFieldType.STRING).description("질권 주소").optional(),
							fieldWithPath("pledge.bondStatus").type(JsonFieldType.STRING)
								.description("증권 송부 여부 (NOT_SENT, SENT, NOT_APPLICABLE)")
								.optional(),
							fieldWithPath("pledge.remark").type(JsonFieldType.STRING).description("질권 비고").optional(),

							// 4. 첨부 서류 (Grouping)
							fieldWithPath("documents").type(JsonFieldType.OBJECT).description("첨부 서류 꾸러미").optional(),

							// 4-1. 사업자등록증
							fieldWithPath("documents.businessLicense").type(JsonFieldType.OBJECT)
								.description("사업자등록증 정보")
								.optional(),
							fieldWithPath("documents.businessLicense.key").type(JsonFieldType.STRING)
								.description("파일 키")
								.optional(),
							fieldWithPath("documents.businessLicense.name").type(JsonFieldType.STRING)
								.description("원본명")
								.optional(),
							fieldWithPath("documents.businessLicense.size").type(JsonFieldType.NUMBER)
								.description("크기")
								.optional(),
							fieldWithPath("documents.businessLicense.extension").type(JsonFieldType.STRING)
								.description("확장자")
								.optional(),

							// 4-2. 발전사업허가증
							fieldWithPath("documents.powerGenerationLicense").type(JsonFieldType.OBJECT)
								.description("발전사업허가증 정보")
								.optional(),
							fieldWithPath("documents.powerGenerationLicense.key").type(JsonFieldType.STRING)
								.description("파일 키")
								.optional(),
							fieldWithPath("documents.powerGenerationLicense.name").type(JsonFieldType.STRING)
								.description("원본명")
								.optional(),
							fieldWithPath("documents.powerGenerationLicense.size").type(JsonFieldType.NUMBER)
								.description("크기")
								.optional(),
							fieldWithPath("documents.powerGenerationLicense.extension").type(JsonFieldType.STRING)
								.description("확장자")
								.optional(),

							// 4-3. 기타 서류들
							fieldWithPath("documents.preUseInspection").type(JsonFieldType.OBJECT)
								.description("사용전 검사 확인증")
								.optional(),
							fieldWithPath("documents.supplyCertificate").type(JsonFieldType.OBJECT)
								.description("공급인증서")
								.optional(),
							fieldWithPath("documents.etc").type(JsonFieldType.OBJECT).description("기타 서류").optional()),
					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	@Test
	@DisplayName("Step 4. 최종 가입 완료")
	void complete() {
		// given
		Long userId = 1L;
		Long appId = 101L;
		DocumentRequest mockSignature = new DocumentRequest("signatures/2025/uuid.png", "sign.png", 512L, "png");

		InsuranceCompleteRequest request = new InsuranceCompleteRequest(mockSignature);

		InsuranceApplication mockApp = createMockApplication(appId, InsuranceStatus.COMPLETED);

		given(insuranceApplicationService.completeApplication(userId, appId, request.toSignatureFile()))
			.willReturn(mockApp);

		// when & then
		webTestClient.post()
			.uri("/v1/insurance/{applicationId}/complete", appId)
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("insurance-complete", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					pathParameters(parameterWithName("applicationId").description("청약서 ID")),
					requestFields(fieldWithPath("signature").type(JsonFieldType.OBJECT).description("전자 서명 파일 정보 (필수)"),
							fieldWithPath("signature.key").type(JsonFieldType.STRING).description("서명 파일 키 (업로드 후 발급)"),
							fieldWithPath("signature.name").type(JsonFieldType.STRING).description("서명 파일명"),
							fieldWithPath("signature.size").type(JsonFieldType.NUMBER).description("서명 파일 크기"),
							fieldWithPath("signature.extension").type(JsonFieldType.STRING).description("서명 파일 확장자")),
					responseFields(getInsuranceResponseFields().toArray(FieldDescriptor[]::new))));
	}

	private List<FieldDescriptor> getInsuranceResponseFields() {
		List<FieldDescriptor> fields = new ArrayList<>();
		// 공통
		fields.addAll(List.of(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 코드"),
				fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("청약서 ID"),
				fieldWithPath("data.applicationNumber").type(JsonFieldType.STRING).description("청약 번호"),
				fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태")));

		// 2. 각 도메인별 필드 추가 (헬퍼 메서드 호출)
		fields.addAll(getApplicantDescriptors("data.applicantInfo"));
		fields.addAll(getAgreementDescriptors("data.agreementInfo"));
		fields.addAll(getPlantDescriptors("data.plantInfo"));
		fields.addAll(getConditionDescriptors("data.conditionInfo"));
		fields.addAll(getCoverageDescriptors("data.coverageInfo"));

		// 문서 정보 (상세 기술 필요 시 Helper 활용)
		fields.add(fieldWithPath("data.documentInfo").type(JsonFieldType.OBJECT).description("첨부 서류 정보").optional());
		fields.addAll(generateFileDescriptors("data.documentInfo.businessLicense", "사업자등록증"));
		fields.addAll(generateFileDescriptors("data.documentInfo.powerGenerationLicense", "발전사업허가증"));
		fields.addAll(generateFileDescriptors("data.documentInfo.preUseInspection", "사용전 검사 확인증"));
		fields.addAll(generateFileDescriptors("data.documentInfo.supplyCertificate", "공급인증서"));
		fields.addAll(generateFileDescriptors("data.documentInfo.etc", "기타 서류"));

		fields.add(fieldWithPath("error").type(JsonFieldType.STRING).optional().description("에러 정보"));

		return fields;
	}

	private List<FieldDescriptor> getApplicantDescriptors(String prefix) {
		return List.of(fieldWithPath(prefix).type(JsonFieldType.OBJECT).description("신청자 정보").optional(),
				fieldWithPath(prefix + ".companyCode").type(JsonFieldType.STRING).description("사업자 번호").optional(),
				fieldWithPath(prefix + ".companyName").type(JsonFieldType.STRING).description("회사명").optional(),
				fieldWithPath(prefix + ".ceoName").type(JsonFieldType.STRING).description("대표자명").optional(),
				fieldWithPath(prefix + ".ceoPhoneNumber").type(JsonFieldType.STRING).description("대표자 연락처").optional(),
				fieldWithPath(prefix + ".applicantName").type(JsonFieldType.STRING).description("신청자명").optional(),
				fieldWithPath(prefix + ".applicantPhoneNumber").type(JsonFieldType.STRING)
					.description("신청자 연락처")
					.optional(),
				fieldWithPath(prefix + ".applicantEmail").type(JsonFieldType.STRING).description("이메일").optional());
	}

	private List<FieldDescriptor> getAgreementDescriptors(String prefix) {
		return List.of(fieldWithPath(prefix).type(JsonFieldType.OBJECT).description("약관 동의 내역").optional(),
				fieldWithPath(prefix + ".re100Interest").type(JsonFieldType.BOOLEAN)
					.description("RE100 관심 여부")
					.optional(),
				fieldWithPath(prefix + ".personalInfoCollectionAgreed").type(JsonFieldType.BOOLEAN)
					.description("개인정보 수집 동의")
					.optional(),
				fieldWithPath(prefix + ".personalInfoThirdPartyAgreed").type(JsonFieldType.BOOLEAN)
					.description("제3자 제공 동의")
					.optional(),
				fieldWithPath(prefix + ".groupRuleAgreed").type(JsonFieldType.BOOLEAN)
					.description("단체규약 동의")
					.optional(),
				fieldWithPath(prefix + ".marketingAgreed").type(JsonFieldType.BOOLEAN).description("마케팅 동의").optional(),
				fieldWithPath(prefix + ".agreedAt").type(JsonFieldType.STRING).description("동의 일시").optional());
	}

	private List<FieldDescriptor> getPlantDescriptors(String prefix) {
		return List.of(fieldWithPath(prefix).type(JsonFieldType.OBJECT).description("발전소 정보").optional(),
				fieldWithPath(prefix + ".name").type(JsonFieldType.STRING).description("발전소명").optional(),
				fieldWithPath(prefix + ".address").type(JsonFieldType.STRING).description("주소").optional(),
				fieldWithPath(prefix + ".region").type(JsonFieldType.STRING).description("지역").optional(),
				fieldWithPath(prefix + ".capacity").type(JsonFieldType.NUMBER).description("용량(kW)").optional(),
				fieldWithPath(prefix + ".area").type(JsonFieldType.NUMBER).description("면적(m²)").optional(),
				fieldWithPath(prefix + ".inspectionDate").type(JsonFieldType.STRING).description("검사일").optional(),
				fieldWithPath(prefix + ".facilityType").type(JsonFieldType.STRING).description("설비 형태").optional(),
				fieldWithPath(prefix + ".driveMethod").type(JsonFieldType.STRING).description("구동 방식").optional(),
				fieldWithPath(prefix + ".salesTarget").type(JsonFieldType.STRING).description("판매처").optional());
	}

	private List<FieldDescriptor> getConditionDescriptors(String prefix) {
		return List.of(fieldWithPath(prefix).type(JsonFieldType.OBJECT).description("가입 조건").optional(),
				fieldWithPath(prefix + ".essInstalled").type(JsonFieldType.BOOLEAN).description("ESS 설치 여부").optional(),
				fieldWithPath(prefix + ".propertyDamageAmount").type(JsonFieldType.NUMBER)
					.description("재물손해 가입금액")
					.optional(),
				fieldWithPath(prefix + ".civilWorkIncluded").type(JsonFieldType.BOOLEAN)
					.description("토목공사 포함 여부")
					.optional(),
				fieldWithPath(prefix + ".liabilityAmount").type(JsonFieldType.NUMBER)
					.description("배상책임 가입금액")
					.optional(),
				fieldWithPath(prefix + ".businessInterruptionAmount").type(JsonFieldType.NUMBER)
					.description("기업휴지 가입금액")
					.optional(),
				fieldWithPath(prefix + ".startDate").type(JsonFieldType.STRING).description("보험 개시일").optional(),

				// 사고 이력 (리스트)
				fieldWithPath(prefix + ".accidents").type(JsonFieldType.ARRAY).description("사고 이력 리스트").optional(),
				fieldWithPath(prefix + ".accidents[].date").type(JsonFieldType.STRING).description("사고 일자").optional(),
				fieldWithPath(prefix + ".accidents[].paymentAmount").type(JsonFieldType.NUMBER)
					.description("사고 보험금")
					.optional(),
				fieldWithPath(prefix + ".accidents[].content").type(JsonFieldType.STRING)
					.description("사고 내용")
					.optional(),

				// 질권 설정 (객체)
				fieldWithPath(prefix + ".pledge").type(JsonFieldType.OBJECT).description("질권 설정 정보").optional(),
				fieldWithPath(prefix + ".pledge.bankName").type(JsonFieldType.STRING).description("질권 은행명").optional(),
				fieldWithPath(prefix + ".pledge.managerName").type(JsonFieldType.STRING).description("담당자명").optional(),
				fieldWithPath(prefix + ".pledge.phone").type(JsonFieldType.STRING).description("담당자 연락처").optional(),
				fieldWithPath(prefix + ".pledge.amount").type(JsonFieldType.NUMBER).description("질권 금액").optional(),
				fieldWithPath(prefix + ".pledge.address").type(JsonFieldType.STRING).description("질권 주소").optional(),
				fieldWithPath(prefix + ".pledge.bondStatus").type(JsonFieldType.STRING)
					.description("증권 송부 상태")
					.optional(),
				fieldWithPath(prefix + ".pledge.remark").type(JsonFieldType.STRING).description("비고").optional());
	}

	private List<FieldDescriptor> getCoverageDescriptors(String prefix) {
		return List.of(fieldWithPath(prefix).type(JsonFieldType.OBJECT).description("보험료 정보").optional(),
				fieldWithPath(prefix + ".mdPremium").type(JsonFieldType.NUMBER).description("재물손해 보험료").optional(),
				fieldWithPath(prefix + ".biPremium").type(JsonFieldType.NUMBER).description("기업휴지 보험료").optional(),
				fieldWithPath(prefix + ".glPremium").type(JsonFieldType.NUMBER).description("배상책임 보험료").optional(),
				fieldWithPath(prefix + ".totalPremium").type(JsonFieldType.NUMBER).description("총 보험료").optional());
	}

	private List<FieldDescriptor> generateFileDescriptors(String path, String description) {
		return List.of(fieldWithPath(path).type(JsonFieldType.OBJECT).description(description).optional(),
				fieldWithPath(path + ".key").type(JsonFieldType.STRING).description("파일 식별자 Key").optional(),
				fieldWithPath(path + ".originalFileName").type(JsonFieldType.STRING).description("원본 파일명").optional(),
				fieldWithPath(path + ".size").type(JsonFieldType.NUMBER).description("파일 크기(Byte)").optional(),
				fieldWithPath(path + ".extension").type(JsonFieldType.STRING).description("확장자").optional());
	}

}