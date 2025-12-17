package com.nexsol.tpa.core.domain;

public interface PaymentCancelRepository {
    PaymentCancel save(PaymentCancel cancel);
    boolean existsByPaymentId(Long paymentId);
}
