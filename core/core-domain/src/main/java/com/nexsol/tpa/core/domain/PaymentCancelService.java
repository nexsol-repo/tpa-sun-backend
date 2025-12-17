package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {

	private final PaymentReader paymentReader;

	private final PaymentValidator paymentValidator;

	private final PaymentCancelAppender paymentCancelAppender;

	private final RefundCalculator refundCalculator;

	public void cancelPayment(Long paymentId, Long userId) {

		Payment payment = paymentReader.read(paymentId);

		paymentValidator.validate(paymentId);

		Long refundAmount = refundCalculator.calculate(payment, LocalDateTime.now());

		PaymentCancel cancel = PaymentCancel.create(payment, refundAmount, "사용자 요청 취소");

		paymentCancelAppender.append(cancel);

	}

}
