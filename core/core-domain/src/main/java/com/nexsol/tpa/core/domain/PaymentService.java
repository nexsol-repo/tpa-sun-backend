package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentReader paymentReader;
    private final PaymentAppender paymentAppender;
    private final InsuranceApplicationReader applicationReader;
;

    // (추후 추가) private final PaymentGatewayClient paymentGatewayClient;

    public void processPayment(Long userId, Long applicationId, Long amount, String method) {
        InsuranceApplication application = applicationReader.read(applicationId);

        Payment payment = Payment.create(applicationId, userId, amount, method);

        //외부 PG사 결제 승인 요청
        // paymentGatewayClient.requestApproval(payment);

        Payment completedPayment = payment.complete();


        paymentAppender.append(completedPayment);

    }

    public Payment getPayment(Long applicationId) {
        return paymentReader.readByApplicationId(applicationId);
    }


}
