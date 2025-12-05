package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record InsuranceCondition(Boolean essInstalled, // ESS 설치 여부 (Wrapper Class 사용으로
														// null 허용 - 작성중 대비)
		Long propertyDamageAmount, // 재물손해 가입금액
		Boolean civilWorkIncluded, // 토목공사 포함 여부
		Long liabilityAmount, // 배상책임 가입금액
		Long businessInterruptionAmount, // 기업휴지 가입금액
		LocalDate startDate, // 보험 개시일
		Boolean accidentHistory, // 최근 5년 사고 이력
		Boolean pledgeSet, // 질권 설정 유무
		PledgeInfo pledgeInfo, // 질권 세부 정보,
		AccidentHistory accidentDetail // 사고 이력
) {
}
