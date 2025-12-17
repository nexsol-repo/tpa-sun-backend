package com.nexsol.tpa.core.domain;

public record PaymentCompletedEvent(Long applicationId, Long paymentId, Long userId, Long amount) {
}
