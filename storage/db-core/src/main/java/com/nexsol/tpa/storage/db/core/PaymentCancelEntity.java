package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.PaymentCancel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCancelEntity extends BaseEntity{
    private Long paymentId;
    private Long userId;
    private Long refundAmount;
    private String reason;
    private LocalDateTime canceledAt;;

    public PaymentCancelEntity(PaymentCancel domain) {
        this.paymentId = domain.paymentId();
        this.userId = domain.userId();
        this.refundAmount = domain.refundAmount();
        this.reason = domain.reason();
        this.canceledAt = domain.canceledAt();
    }

    public PaymentCancel toDomain() {
        return PaymentCancel.builder()
                .id(getId())
                .paymentId(paymentId)
                .userId(userId)
                .refundAmount(refundAmount)
                .reason(reason)
                .canceledAt(canceledAt)
                .build();
    }
}
