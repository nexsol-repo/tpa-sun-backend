package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.BondSendStatus;
import com.nexsol.tpa.core.enums.InsuranceDocumentType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record InsuranceConditionRequest(Boolean essInstalled, Long propertyDamageAmount, Boolean civilWorkIncluded,
		Long liabilityAmount, Long businessInterruptionAmount, LocalDate startDate,

		// 사고 이력
		Boolean accidentHistory, LocalDate accidentDate, Long accidentPayment, String accidentContent,

		// 질권 설정
		Boolean pledgeSet, String pledgeBankName, String pledgeManagerName, String pledgeManagerPhone,
		Long pledgeAmount, String pledgeAddress, BondSendStatus pledgeBondStatus, String pledgeRemark,
		// 첨부서류
		DocumentRequest businessLicense, DocumentRequest powerGenerationLicense, DocumentRequest preUseInspection,
		DocumentRequest supplyCertificate, DocumentRequest etc

) {

	public InsuranceDocument toInsuranceDocument() {
		List<InsuranceAttachment> attachments = new ArrayList<>();
		addAttachment(attachments, businessLicense, InsuranceDocumentType.BUSINESS_LICENSE);
		addAttachment(attachments, powerGenerationLicense, InsuranceDocumentType.POWER_GEN_LICENSE);
		addAttachment(attachments, preUseInspection, InsuranceDocumentType.PRE_USE_INSPECTION);
		addAttachment(attachments, supplyCertificate, InsuranceDocumentType.SUPPLY_CERTIFICATE);
		addAttachment(attachments, etc, InsuranceDocumentType.ETC);

		return InsuranceDocument.builder().attachments(attachments).build();
	}

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

	private void addAttachment(List<InsuranceAttachment> list, DocumentRequest req, InsuranceDocumentType type) {
		if (req != null && req.key() != null) {

			list.add(InsuranceAttachment.builder().type(type).file(req.toDocumentFile()).build());
		}
	}
}
