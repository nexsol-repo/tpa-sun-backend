package com.nexsol.tpa.core.domain;

import java.util.Optional;

public interface PaymentRepository {

	Payment save(Payment payment);

	Optional<Payment> findById(Long id);

	Optional<Payment> findByApplicationId(Long applicationId);

}
