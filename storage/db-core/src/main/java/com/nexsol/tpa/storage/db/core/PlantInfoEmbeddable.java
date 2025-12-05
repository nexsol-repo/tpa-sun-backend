package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsurancePlant;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
public class PlantInfoEmbeddable {

	private String plantName;

	private String plantAddress;

	private String plantRegion;

	private BigDecimal plantCapacity;

	private BigDecimal plantArea;

	private LocalDate plantInspectionDate;

	private String plantFacilityType;

	private String plantDriveMethod;

	private String plantSalesTarget;

	public PlantInfoEmbeddable(InsurancePlant domain) {
		this.plantName = domain.plantName();
		this.plantAddress = domain.address();
		this.plantRegion = domain.region();
		this.plantCapacity = domain.capacity();
		this.plantArea = domain.area();
		this.plantInspectionDate = domain.inspectionDate();
		this.plantFacilityType = domain.facilityType();
		this.plantDriveMethod = domain.driveMethod();
		this.plantSalesTarget = domain.salesTarget();
	}

	public InsurancePlant toDomain() {
		return InsurancePlant.builder()
			.plantName(plantName)
			.address(plantAddress)
			.region(plantRegion)
			.capacity(plantCapacity)
			.area(plantArea)
			.inspectionDate(plantInspectionDate)
			.facilityType(plantFacilityType)
			.driveMethod(plantDriveMethod)
			.salesTarget(plantSalesTarget)
			.build();
	}

}
