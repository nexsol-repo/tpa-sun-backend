package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final PaymentCancelReader paymentCancelReader;

    public void validate(Long paymentId) {
        boolean alreadyCancelled = paymentCancelReader.exists(paymentId);

        if (alreadyCancelled) {
            throw new CoreException(CoreErrorType.PAYMENT_ALREADY_CANCELLED);
        }
    }
}
