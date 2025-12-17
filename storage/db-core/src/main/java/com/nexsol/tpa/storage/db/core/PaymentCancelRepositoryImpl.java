package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.PaymentCancel;
import com.nexsol.tpa.core.domain.PaymentCancelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentCancelRepositoryImpl implements PaymentCancelRepository {

	private final PaymentCancelJpaRepository paymentCancelJpaRepository;

	@Override
	public PaymentCancel save(PaymentCancel cancel) {
		return paymentCancelJpaRepository.save(new PaymentCancelEntity(cancel)).toDomain();
	}

	@Override
	public boolean existsByPaymentId(Long paymentId) {
		return paymentCancelJpaRepository.existsByPaymentId(paymentId);
	}

}
