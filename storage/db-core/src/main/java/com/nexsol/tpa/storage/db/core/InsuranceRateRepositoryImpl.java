package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsuranceRateRepository;
import com.nexsol.tpa.core.enums.RateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InsuranceRateRepositoryImpl implements InsuranceRateRepository {

	private final InsuranceRateJpaRepository insuranceRateJpaRepository;

	@Override
	public Optional<BigDecimal> findRate(RateType type, String key, LocalDate date) {
		return insuranceRateJpaRepository.findRate(type, key, date).map(InsuranceRateEntity::getRateValue);
	}

}
