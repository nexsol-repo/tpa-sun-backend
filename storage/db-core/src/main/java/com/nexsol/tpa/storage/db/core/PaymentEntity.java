package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.Payment;
import com.nexsol.tpa.core.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity extends BaseEntity{
    private Long applicationId;
    private Long userId;
    private Long amount;
    private String method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    public PaymentEntity(Payment payment) {
        this.applicationId = payment.applicationId();
        this.userId = payment.userId();
        this.amount = payment.amount();
        this.method = payment.method();
        this.status = payment.status();
        this.paidAt = payment.paidAt();
    }


    public void update(Payment payment) {
        this.status = payment.status();
        this.paidAt = payment.paidAt();
    }

    public Payment toDomain() {
        return Payment.builder()
                .id(getId())
                .applicationId(applicationId)
                .userId(userId)
                .amount(amount)
                .method(method)
                .status(status)
                .paidAt(paidAt)
                .build();
    }
}
