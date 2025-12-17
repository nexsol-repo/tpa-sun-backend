package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;

/**
 * @param essInstalled ess 설치여부
 * @param propertyDamageAmount 재물 가입금액
 * @param civilWorkIncluded 토목공사 포함 여부
 * @param liabilityAmount 배상가입금액
 * @param businessInterruptionAmount 휴지 가입 금액
 */
@Builder(toBuilder = true)
public record InsuranceCondition(boolean essInstalled, Long propertyDamageAmount, boolean civilWorkIncluded,
		Long liabilityAmount, Long businessInterruptionAmount, LocalDate startDate, LocalDate endDate,
		Accident accident, Pledge pledge

) {
	public boolean hasAccident() {
		return accident != null;
	}
}
