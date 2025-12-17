package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentReader paymentReader;

	private final PaymentAppender paymentAppender;

	private final InsuranceApplicationReader applicationReader;

	private final ApplicationEventPublisher eventPublisher;

	;

	// (추후 추가) private final PaymentGatewayClient paymentGatewayClient;

	public void processPayment(Long userId, Long applicationId, Long amount, String method) {
		InsuranceApplication application = applicationReader.read(applicationId);

		Payment payment = Payment.create(applicationId, userId, amount, method);

		// 외부 PG사 결제 승인 요청
		// paymentGatewayClient.requestApproval(payment);

		Payment completedPayment = payment.complete();

		Payment savedPayment = paymentAppender.append(completedPayment);

		eventPublisher.publishEvent(new PaymentCompletedEvent(savedPayment.applicationId(), savedPayment.id(),
				savedPayment.userId(), savedPayment.amount()));

	}

	public Payment getPayment(Long applicationId) {
		return paymentReader.readByApplicationId(applicationId);
	}

}
