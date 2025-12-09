package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * @param essInstalled ess 설치여부
 * @param propertyDamageAmount 재물 가입금액
 * @param civilWorkIncluded 토목공사 포함 여부
 * @param liabilityAmount 배상가입금액
 * @param businessInterruptionAmount 휴지 가입 금액
 */
@Builder
public record JoinCondition(boolean essInstalled, Long propertyDamageAmount, boolean civilWorkIncluded,
		Long liabilityAmount, Long businessInterruptionAmount, LocalDate startDate, List<Accident> accidents,
		Pledge pledge

) {
	public JoinCondition {
		if (accidents == null)
			accidents = Collections.emptyList();
	}

	public boolean hasAccidents() {
		return accidents != null && !accidents.isEmpty();
	}
}
