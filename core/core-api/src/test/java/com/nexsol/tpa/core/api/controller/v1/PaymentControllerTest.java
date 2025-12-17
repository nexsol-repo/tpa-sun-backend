package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.PaymentCancelRequest;
import com.nexsol.tpa.core.api.controller.v1.request.PaymentRequest;
import com.nexsol.tpa.core.domain.Payment;
import com.nexsol.tpa.core.domain.PaymentCancelService;
import com.nexsol.tpa.core.domain.PaymentService;
import com.nexsol.tpa.core.enums.PaymentStatus;
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

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class PaymentControllerTest extends RestDocsTest {

	private final PaymentService paymentService = mock(PaymentService.class);

	private final PaymentCancelService paymentCancelService = mock(PaymentCancelService.class);

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

		// 2. MockController 직접 빌드
		this.webTestClient = MockMvcWebTestClient
			.bindToController(new PaymentController(paymentService, paymentCancelService))
			.customArgumentResolvers(authPrincipalResolver)
			.configureClient()
			.filter(documentationConfiguration(restDocumentation))
			.build();
	}

	@Test
	@DisplayName("결제 처리 API 문서화")
	void processPayment() {
		// given
		Long userId = 1L;
		PaymentRequest request = new PaymentRequest(100L, 150000L, "CARD");

		doNothing().when(paymentService)
			.processPayment(eq(userId), eq(request.applicationId()), eq(request.amount()), eq(request.method()));

		// when & then
		webTestClient.post()
			.uri("/v1/payments")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("payment-process", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					requestFields(fieldWithPath("applicationId").type(JsonFieldType.NUMBER).description("청약서 ID"),
							fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
							fieldWithPath("method").type(JsonFieldType.STRING).description("결제 수단 (CARD, TRANSFER 등)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)").optional(),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보").optional())));
	}

	@Test
	@DisplayName("결제 취소 API 문서화")
	void cancelPayment() {
		// given
		Long userId = 1L;
		Long paymentId = 50L;
		PaymentCancelRequest request = new PaymentCancelRequest("사용자 요청에 의한 취소");

		doNothing().when(paymentCancelService).cancelPayment(eq(paymentId), eq(userId));

		// when & then
		webTestClient.post()
			.uri("/v1/payments/{paymentId}/cancel", paymentId)
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("payment-cancel", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					pathParameters(parameterWithName("paymentId").description("취소할 결제 ID")),
					requestFields(fieldWithPath("reason").type(JsonFieldType.STRING).description("취소 사유")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터").optional(),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보").optional())));
	}

	@Test
	@DisplayName("신청서 ID로 결제 내역 조회 API 문서화")
	void getPayment() {
		// given
		Long applicationId = 100L;

		Payment mockPayment = Payment.builder()
			.id(1L)
			.applicationId(applicationId)
			.userId(1L)
			.amount(150000L)
			.method("CARD")
			.status(PaymentStatus.COMPLETED)
			.paidAt(LocalDateTime.of(2023, 12, 25, 14, 0, 0))
			.build();

		given(paymentService.getPayment(applicationId)).willReturn(mockPayment);

		// when & then
		webTestClient.get()
			.uri("/v1/payments/by-application/{applicationId}", applicationId)
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("payment-get", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					pathParameters(parameterWithName("applicationId").description("조회할 청약서 ID")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
							fieldWithPath("data.paymentId").type(JsonFieldType.NUMBER).description("결제 ID"),
							fieldWithPath("data.applicationId").type(JsonFieldType.NUMBER).description("청약서 ID"),
							fieldWithPath("data.amount").type(JsonFieldType.NUMBER).description("결제 금액"),
							fieldWithPath("data.method").type(JsonFieldType.STRING).description("결제 수단"),
							fieldWithPath("data.status").type(JsonFieldType.STRING)
								.description("결제 상태 (PENDING, COMPLETED 등)"),
							fieldWithPath("data.paidAt").type(JsonFieldType.STRING).description("결제 승인 일시").optional(),
							fieldWithPath("data.canceledAt").type(JsonFieldType.STRING)
								.description("결제 취소 일시")
								.optional(),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보").optional())));
	}

}
