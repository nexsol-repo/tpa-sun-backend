package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.PaymentCancelRequest;
import com.nexsol.tpa.core.api.controller.v1.request.PaymentRequest;
import com.nexsol.tpa.core.api.controller.v1.response.PaymentResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.domain.Payment;
import com.nexsol.tpa.core.domain.PaymentCancelService;
import com.nexsol.tpa.core.domain.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	private final PaymentCancelService paymentCancelService;

	@PostMapping
	public ApiResponse<Object> processPayment(@AuthenticationPrincipal Long userId,
			@RequestBody @Valid PaymentRequest request) {
		paymentService.processPayment(userId, request.applicationId(), request.amount(), request.method());
		return ApiResponse.success();
	}

	@PostMapping("/{paymentId}/cancel")
	public ApiResponse<Object> cancelPayment(@AuthenticationPrincipal Long userId, @PathVariable Long paymentId,
			@RequestBody @Valid PaymentCancelRequest request) {
		paymentCancelService.cancelPayment(paymentId, userId);
		return ApiResponse.success();
	}

	@GetMapping("/by-application/{applicationId}")
	public ApiResponse<PaymentResponse> getPayment(@PathVariable Long applicationId) {
		Payment payment = paymentService.getPayment(applicationId);
		return ApiResponse.success(PaymentResponse.of(payment));
	}

}
