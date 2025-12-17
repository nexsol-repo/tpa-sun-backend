package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.Payment;
import com.nexsol.tpa.core.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentCancelJpaRepository paymentCancelJpaRepository;
    @Override
    public Payment save(Payment payment) {
        if (payment.id() == null) {
            return paymentJpaRepository.save(new PaymentEntity(payment)).toDomain();
        }

        PaymentEntity entity = paymentJpaRepository.findById(payment.id())
                .orElseThrow();
        entity.update(payment);
        return entity.toDomain();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id).map(PaymentEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByApplicationId(Long applicationId) {
        return paymentJpaRepository.findByApplicationId(applicationId).map(PaymentEntity::toDomain);
    }

}
