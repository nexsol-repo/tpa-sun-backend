package com.nexsol.tpa.core.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class RefundCalculator {

	/**
	 * 책임: 정책(15일 기준)에 따라 환불 금액을 계산한다.
	 */
	public Long calculate(Payment payment, LocalDateTime requestTime) {
		long daysElapsed = ChronoUnit.DAYS.between(payment.paidAt(), requestTime);

		// 1. 가입취소 (15일 이내): 전액 환불
		if (daysElapsed <= 15) {
			return payment.amount();
		}

		// 2. 중도해지 (16일 이후): 일할 계산 (단순 예시)
		// 실제로는 (총 보험료 / 365) * 남은 일수 등의 로직 적용
		// 여기서는 예시로 50% 공제한다고 가정
		return (long) (payment.amount() * 0.5);
	}

}