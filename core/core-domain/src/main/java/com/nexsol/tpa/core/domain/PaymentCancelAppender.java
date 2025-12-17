package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCancelAppender {

	private final PaymentCancelRepository paymentCancelRepository;

	public PaymentCancel append(PaymentCancel paymentCancel) {
		return paymentCancelRepository.save(paymentCancel);
	}

}
