package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentHistory;
import com.nexsol.tpa.core.domain.InsuranceCondition;
import com.nexsol.tpa.core.domain.PledgeInfo;
import com.nexsol.tpa.core.enums.BondSendStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
public class ConditionEmbeddable {

	private Boolean essInstalled;

	private Long propertyDamageAmount;

	private Boolean civilWorkIncluded;

	private Long liabilityAmount;

	private Long businessInterruptionAmount;

	private LocalDate insuranceStartDate;

	// 사고 이력
	private Boolean accidentHistory;

	private LocalDate accidentDate;

	private Long accidentPayment;

	private String accidentContent;

	// 질권 설정
	private Boolean pledgeSet;

	private String pledgeBankName;

	private String pledgeManagerName;

	private String pledgeManagerPhone;

	private Long pledgeAmount;

	private String pledgeAddress;

	@Enumerated(EnumType.STRING)
	private BondSendStatus pledgeBondStatus;

	private String pledgeRemark;

	public ConditionEmbeddable(InsuranceCondition domain) {
		this.essInstalled = domain.essInstalled();
		this.propertyDamageAmount = domain.propertyDamageAmount();
		this.civilWorkIncluded = domain.civilWorkIncluded();
		this.liabilityAmount = domain.liabilityAmount();
		this.businessInterruptionAmount = domain.businessInterruptionAmount();
		this.insuranceStartDate = domain.startDate();
		this.accidentHistory = domain.accidentHistory();

		if (domain.accidentDetail() != null) {
			this.accidentDate = domain.accidentDetail().accidentDate();
			this.accidentPayment = domain.accidentDetail().insurancePayment();
			this.accidentContent = domain.accidentDetail().accidentContent();
		}

		this.pledgeSet = domain.pledgeSet();
		if (domain.pledgeInfo() != null) {
			this.pledgeBankName = domain.pledgeInfo().bankName();
			this.pledgeManagerName = domain.pledgeInfo().managerName();
			this.pledgeManagerPhone = domain.pledgeInfo().managerPhone();
			this.pledgeAmount = domain.pledgeInfo().amount();
			this.pledgeAddress = domain.pledgeInfo().address();
			this.pledgeBondStatus = domain.pledgeInfo().bondSendStatus();
			this.pledgeRemark = domain.pledgeInfo().remark();
		}
	}

	public InsuranceCondition toDomain() {
		AccidentHistory history = null;
		if (Boolean.TRUE.equals(this.accidentHistory)) {
			history = AccidentHistory.builder()
				.accidentDate(accidentDate)
				.insurancePayment(accidentPayment)
				.accidentContent(accidentContent)
				.build();
		}

		PledgeInfo pledgeInfo = null;
		if (Boolean.TRUE.equals(this.pledgeSet)) {
			pledgeInfo = PledgeInfo.builder()
				.bankName(pledgeBankName)
				.managerName(pledgeManagerName)
				.managerPhone(pledgeManagerPhone)
				.amount(pledgeAmount)
				.address(pledgeAddress)
				.bondSendStatus(pledgeBondStatus)
				.remark(pledgeRemark)
				.build();
		}

		return InsuranceCondition.builder()
			.essInstalled(essInstalled)
			.propertyDamageAmount(propertyDamageAmount)
			.civilWorkIncluded(civilWorkIncluded)
			.liabilityAmount(liabilityAmount)
			.businessInterruptionAmount(businessInterruptionAmount)
			.startDate(insuranceStartDate)
			.accidentHistory(accidentHistory)
			.accidentDetail(history)
			.pledgeSet(pledgeSet)
			.pledgeInfo(pledgeInfo)
			.build();
	}

}
