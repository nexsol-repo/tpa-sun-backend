package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentAppender {
    private final PaymentRepository paymentRepository;

    public Payment append(Payment payment) {
        return paymentRepository.save(payment);
    }
}
