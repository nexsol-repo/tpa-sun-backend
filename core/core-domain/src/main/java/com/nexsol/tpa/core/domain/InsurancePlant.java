package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InsurancePlant(String name, String address, String region, BigDecimal capacity, BigDecimal area,
		LocalDate inspectionDate, String facilityType, String driveMethod, String salesTarget) {

	public BigDecimal getEffectiveArea() {
		if (area == null || area.compareTo(BigDecimal.ZERO) == 0) {
			return capacity.multiply(BigDecimal.valueOf(4.93));
		}
		return area;
	}
}
