package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.InsurancePlant;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsurancePlantRequest(
        String plantName,
        String address,
        String region,        // Daum API의 'sido' 값 (부산, 전남, 서울...)
        BigDecimal capacity,
        BigDecimal area,
        LocalDate inspectionDate,
        String facilityType,
        String driveMethod,
        String salesTarget
) {
    public InsurancePlant toInsurancePlant(){
        return InsurancePlant.builder()
                .plantName(plantName)
                .address(address)
                .region(region)
                .capacity(capacity)
                .area(area)
                .inspectionDate(inspectionDate)
                .facilityType(facilityType)
                .driveMethod(driveMethod)
                .salesTarget(salesTarget)
                .build();
    }
}
