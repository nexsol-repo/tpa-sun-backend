package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsuranceCoverage;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class CoverageEmbeddable {

	private Long deductiblePd;

	private Long deductibleLi;

	private Long deductibleBi;

	private Long totalPremium;

	public CoverageEmbeddable(InsuranceCoverage domain) {
		this.deductiblePd = domain.propertyDamageDeductible();
		this.deductibleLi = domain.liabilityDeductible();
		this.deductibleBi = domain.businessInterruptionDeductible();
		this.totalPremium = domain.totalPremium();
	}

	public InsuranceCoverage toDomain() {
		return InsuranceCoverage.builder()
			.propertyDamageDeductible(deductiblePd)
			.liabilityDeductible(deductibleLi)
			.businessInterruptionDeductible(deductibleBi)
			.totalPremium(totalPremium)
			.build();
	}

}
