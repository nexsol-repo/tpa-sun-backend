package com.nexsol.tpa.core.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component

public class InsuranceConditionPolicy {

	public InsuranceCondition enforceDuration(InsuranceCondition condition) {
		LocalDate startDate = condition.startDate();

		if (startDate != null) {
			LocalDate endDate = startDate.plusYears(1).minusDays(1);

			return condition.toBuilder().endDate(endDate).build();
		}
		return condition;
	}

}
