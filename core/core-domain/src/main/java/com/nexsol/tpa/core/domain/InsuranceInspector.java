package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import org.springframework.stereotype.Component;

@Component
public class InsuranceInspector {
    public void inspectCondition(InsuranceCondition condition) {
        if (condition == null) return;

        boolean isEssInstalled = Boolean.TRUE.equals(condition.essInstalled());
        long pdAmount = condition.propertyDamageAmount() != null ? condition.propertyDamageAmount() : 0;
        long liabilityAmount = condition.liabilityAmount() != null ? condition.liabilityAmount() : 0;

        if (isEssInstalled && pdAmount >= 3_000_000_000L && liabilityAmount > 1_000_000_000L) {
            throw new CoreException(CoreErrorType.INSURANCE_MANUAL_CONSULTATION_REQUIRED);
        }
    }
}
