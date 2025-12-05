package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.RateType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InsuranceRateReader {
    private final InsuranceRateRepository insuranceRateRepository;

    public BigDecimal read(RateType type, String key){
        return insuranceRateRepository.findRate(type, key, LocalDate.now())
                .orElseThrow(()->new CoreException(CoreErrorType.INSURANCE_RATE_NOT_FOUND_DATA));
    }
}
