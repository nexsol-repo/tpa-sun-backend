package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCancelJpaRepository extends JpaRepository<PaymentCancelEntity, Long> {

	boolean existsByPaymentId(Long paymentId);

}
