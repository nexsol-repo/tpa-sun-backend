package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentCancel(
        Long id,
        Long paymentId,
        Long userId,
        Long refundAmount,
        String reason,
        LocalDateTime canceledAt
) {

    public static PaymentCancel create(Payment payment, Long refundAmount, String reason) {
        return PaymentCancel.builder()
                .paymentId(payment.id())
                .userId(payment.userId())
                .refundAmount(refundAmount)
                .reason(reason)
                .canceledAt(LocalDateTime.now())
                .build();
    }
}
