package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsurancePlant;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
public class InsurancePlantEmbeddable {

	private String plantName;

	@Column(name = "plant_address")
	private String address;

	@Column(name = "plant_region")
	private String region;

	@Column(name = "plant_capacity")
	private BigDecimal capacity;

	@Column(name = "plant_area")
	private BigDecimal area;

	@Column(name = "plant_inspection_date")
	private LocalDate inspectionDate;

	@Column(name = "plant_facility_type")
	private String facilityType;

	@Column(name = "plant_drive_method")
	private String driveMethod;

	@Column(name = "plant_sales_target")
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
