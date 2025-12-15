package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.BondSendStatus;
import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record InsuranceConditionRequest(@NotNull Boolean essInstalled, Long propertyDamageAmount,
		@NotNull Boolean civilWorkIncluded, Long liabilityAmount, Long businessInterruptionAmount, LocalDate startDate,
		@Valid AccidentRequest accident,

		@Valid PledgeRequest pledge,

		@Valid DocumentSetRequest documents) {

	public JoinCondition toJoinCondition() {
		return JoinCondition.builder()
			.essInstalled(essInstalled)
			.propertyDamageAmount(propertyDamageAmount)
			.civilWorkIncluded(civilWorkIncluded)
			.businessInterruptionAmount(businessInterruptionAmount)
			.liabilityAmount(liabilityAmount)
			.startDate(startDate)
			.accident(resolveAccident())
			.pledge(resolvePledge())
			.build();
	}

	private Accident resolveAccident() {
		if (this.accident == null)
			return null;
		return this.accident.toAccident();
	}

	private Pledge resolvePledge() {
		if (this.pledge == null)
			return null;
		return this.pledge.toPledge();
	}

	public InsuranceDocument toInsuranceDocument() {
		if (this.documents == null) {
			return InsuranceDocument.builder().attachments(Collections.emptyList()).build();
		}
		return this.documents.toDocument();
	}

}
