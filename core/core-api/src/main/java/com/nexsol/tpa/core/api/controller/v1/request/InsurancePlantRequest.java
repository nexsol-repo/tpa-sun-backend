package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.InsurancePlant;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InsurancePlantRequest(String plantName, String address, String region, BigDecimal capacity,
                                    BigDecimal area, LocalDate inspectionDate, String facilityType, String driveMethod,
                                    String salesTarget) {
    public InsurancePlant toInsuredPlant() {
        return InsurancePlant.builder()
                .name(plantName)
                .address(address)
                .region(region)
                .capacity(capacity != null ? capacity : BigDecimal.ZERO)
                .area(area != null ? area : BigDecimal.ZERO)
                .inspectionDate(inspectionDate)
                .facilityType(facilityType)
                .driveMethod(driveMethod)
                .salesTarget(salesTarget)
                .build();
    }
}
