package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsurancePlant;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
public class InsurancePlantEmbeddable {

	private String plantName;

	private String address;

	private String region;

	private BigDecimal capacity;

	private BigDecimal area;

	private LocalDate inspectionDate;

	private String facilityType;

	private String driveMethod;

	private String salesTarget;

	public InsurancePlantEmbeddable(InsurancePlant domain) {
		if (domain == null)
			return;
		this.plantName = domain.name();
		this.address = domain.address();
		this.region = domain.region();
		this.capacity = domain.capacity();
		this.area = domain.area();
		this.inspectionDate = domain.inspectionDate();
		this.facilityType = domain.facilityType();
		this.driveMethod = domain.driveMethod();
		this.salesTarget = domain.salesTarget();
	}

	public InsurancePlant toDomain() {
		return InsurancePlant.builder()
			.name(plantName)
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
