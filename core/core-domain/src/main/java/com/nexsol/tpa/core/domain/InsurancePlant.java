package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InsurancePlant(String name, String address, String region, BigDecimal capacity, BigDecimal area,
		LocalDate inspectionDate, String facilityType, String driveMethod, String salesTarget) {


}
