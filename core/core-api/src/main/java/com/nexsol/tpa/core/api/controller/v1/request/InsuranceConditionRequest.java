package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.AccidentHistory;
import com.nexsol.tpa.core.domain.InsuranceCondition;
import com.nexsol.tpa.core.domain.PledgeInfo;
import com.nexsol.tpa.core.enums.BondSendStatus;

import java.time.LocalDate;

public record InsuranceConditionRequest(Boolean essInstalled, Long propertyDamageAmount, Boolean civilWorkIncluded,
		Long liabilityAmount, Long businessInterruptionAmount, LocalDate startDate,

		// 사고 이력
		Boolean accidentHistory, LocalDate accidentDate, Long accidentPayment, String accidentContent,

		// 질권 설정
		Boolean pledgeSet, String pledgeBankName, String pledgeManagerName, String pledgeManagerPhone,
		Long pledgeAmount, String pledgeAddress, BondSendStatus pledgeBondStatus, String pledgeRemark

) {

	public InsuranceCondition toInsuranceCondition() {

		AccidentHistory accidentHistoryDetail = null;

		if (Boolean.TRUE.equals(accidentHistory)) {
			accidentHistoryDetail = AccidentHistory.builder()
				.accidentDate(accidentDate)
				.insurancePayment(accidentPayment)
				.accidentContent(accidentContent)
				.build();
		}

		PledgeInfo pledgeInfo = null;
		if (Boolean.TRUE.equals(pledgeSet)) {
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
			.startDate(startDate)
			.accidentHistory(accidentHistory)
			.accidentDetail(accidentHistoryDetail)
			.pledgeSet(pledgeSet)
			.pledgeInfo(pledgeInfo)
			.build();
	}
}
