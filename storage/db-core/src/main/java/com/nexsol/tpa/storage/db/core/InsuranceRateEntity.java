package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.enums.RateType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "insurance_rate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsuranceRateEntity extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RateType rateType;

	@Column(nullable = false)
	private String rateKey;

	@Column(nullable = false, precision = 10, scale = 5)
	private BigDecimal rateValue;

	private LocalDate effectiveDate;

}
