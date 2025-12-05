package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InsurancePlant(
String plantName,
String address,
String region,
BigDecimal capacity,
BigDecimal area,
LocalDate inspectionDate,
String facilityType,
String driveMethod,
String salesTarget
){

    public InsurancePlant {
        // [검증] 용량이나 면적은 음수일 수 없음
        if (capacity != null && capacity.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(CoreErrorType.INVALID_INPUT, "설비 용량은 0 이상이어야 합니다.");
        }
    }
}
