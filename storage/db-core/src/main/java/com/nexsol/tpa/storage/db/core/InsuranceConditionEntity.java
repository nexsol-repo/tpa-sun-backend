package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.Accident;
import com.nexsol.tpa.core.domain.JoinCondition;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "insurance_condition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsuranceConditionEntity extends BaseEntity {

	@Column(nullable = false)
	private Long applicationId;

	private boolean essInstalled;

	private Long propertyDamageAmount;

	@Column(nullable = false)
	private boolean civilWorkIncluded;

	private Long liabilityAmount;

	private Long businessInterruptionAmount;

	private LocalDate startDate;

	@Embedded
	private PledgeEmbeddable pledge; // 질권은 1:1이므로 임베디드로 포함

	public static InsuranceConditionEntity fromDomain(JoinCondition domain, Long applicationId) {
		InsuranceConditionEntity entity = new InsuranceConditionEntity();
		entity.applicationId = applicationId;
		entity.update(domain);
		return entity;
	}

	public void update(JoinCondition domain) {
		this.essInstalled = domain.essInstalled();
		this.propertyDamageAmount = domain.propertyDamageAmount();
		this.civilWorkIncluded = domain.civilWorkIncluded();
		this.liabilityAmount = domain.liabilityAmount();
		this.businessInterruptionAmount = domain.businessInterruptionAmount();
		this.startDate = domain.startDate();

		if (domain.pledge() != null) {
			this.pledge = new PledgeEmbeddable(domain.pledge());
		}
		else {
			this.pledge = null;
		}
	}

	public JoinCondition toDomain(Accident accident) {
		return JoinCondition.builder()
			.essInstalled(essInstalled)
			.propertyDamageAmount(propertyDamageAmount)
			.civilWorkIncluded(civilWorkIncluded)
			.liabilityAmount(liabilityAmount)
			.businessInterruptionAmount(businessInterruptionAmount)
			.startDate(startDate)
			.pledge(pledge != null ? pledge.toDomain() : null)
			.accident(accident) // 별도 조회한 사고 이력 주입
			.build();
	}

}