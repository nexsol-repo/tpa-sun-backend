package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCancelReader {

	private final PaymentCancelRepository paymentCancelRepository;

	public boolean exists(Long paymentId) {
		return paymentCancelRepository.existsByPaymentId(paymentId);
	}

}
