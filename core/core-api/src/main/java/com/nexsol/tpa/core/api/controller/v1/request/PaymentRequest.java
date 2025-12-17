package com.nexsol.tpa.core.api.controller.v1.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "보험 신청서 Id는 필수 입니다.")
        Long applicationId,

        @NotNull(message = "결제 금액은 필수입니다.")
        @Min(value = 1, message = "결제 금액은 0원보다 커야 합니다.")
        Long amount,

        @NotBlank(message = "결제 수단은 필수입니다.")
        String method // CARD, TRANSFER 등

) {
}
