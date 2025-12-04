package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record InsuranceCoverage(
        long propertyDamageDeductible,      // 재물손해 자기부담금
        long liabilityDeductible,           // 배상책임 자기부담금
        long businessInterruptionDeductible,// 기업휴지 자기부담금 (일수 등)
        long totalPremium
) {
}
