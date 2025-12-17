package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.Payment;
import com.nexsol.tpa.core.enums.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentResponse(

		Long paymentId, Long applicationId, Long amount, String method, PaymentStatus status, LocalDateTime paidAt,
		LocalDateTime canceledAt) {

	public static PaymentResponse of(Payment payment) {
		if (payment == null)
			return null;

		return PaymentResponse.builder()
			.paymentId(payment.id())
			.applicationId(payment.applicationId())
			.amount(payment.amount())
			.method(payment.method())
			.status(payment.status())
			.paidAt(payment.paidAt())
			.canceledAt(payment.cancelledAt()) // 도메인에 필드가 있다면 매핑
			.build();
	}
}
