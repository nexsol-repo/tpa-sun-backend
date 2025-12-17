package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record Payment(Long id, Long applicationId, Long userId, Long amount, String method, PaymentStatus status,
		LocalDateTime paidAt, LocalDateTime cancelledAt) {

	public static Payment create(Long applicationId, Long userId, Long amount, String method) {
		return Payment.builder()
			.applicationId(applicationId)
			.userId(userId)
			.amount(amount)
			.method(method)
			.status(PaymentStatus.PENDING)
			.build();
	}

	public Payment complete() {
		return this.toBuilder()
			.status(PaymentStatus.COMPLETED)
			.paidAt(LocalDateTime.now()) // 승인 시점 기록
			.build();
	}

	// 비즈니스 행위: 결제 취소 처리
	public Payment cancel() {
		return this.toBuilder().status(PaymentStatus.PENDING).cancelledAt(LocalDateTime.now()).build();
	}
}
