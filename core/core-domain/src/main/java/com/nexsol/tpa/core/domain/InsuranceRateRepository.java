package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface InsuranceRateRepository {
    Optional<BigDecimal> findRate(RateType type, String key, LocalDate date);
}
