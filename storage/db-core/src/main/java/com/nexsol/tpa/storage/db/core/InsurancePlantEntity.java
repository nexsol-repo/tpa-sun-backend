package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsurancePlant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "insurance_plant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsurancePlantEntity extends BaseEntity {

	@Column(nullable = false)
	private Long applicationId;

	private String plantName;

	private String address;

	private String region;

	private BigDecimal capacity;

	private BigDecimal area;

	private LocalDate inspectionDate;

	private String facilityType;

	private String driveMethod;

	private String salesTarget;

	public static InsurancePlantEntity fromDomain(InsurancePlant domain, Long applicationId) {
		InsurancePlantEntity entity = new InsurancePlantEntity();
		entity.applicationId = applicationId;
		entity.plantName = domain.name();
		entity.address = domain.address();
		entity.region = domain.region();
		entity.capacity = domain.capacity();
		entity.area = domain.area();
		entity.inspectionDate = domain.inspectionDate();
		entity.facilityType = domain.facilityType();
		entity.driveMethod = domain.driveMethod();
		entity.salesTarget = domain.salesTarget();
		return entity;
	}

	public void update(InsurancePlant domain) {
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
