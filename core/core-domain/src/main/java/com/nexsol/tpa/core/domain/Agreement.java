package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Agreement(
		// 1. RE100 관심 여부 (선택)
		boolean re100Interest,

		// 2. 필수 동의 그룹 (법적 효력을 위해 개별 저장 권장)
		boolean personalInfoCollectionAgreed, // 개인(신용)정보 수집 이용
		boolean personalInfoThirdPartyAgreed, // 제3자 제공
		boolean groupRuleAgreed, // 단체규약 및 유의사항

		// 3. 마케팅 동의 (선택)
		boolean marketingAgreed,

		// 동의 일시 (Audit 용도)
		LocalDateTime agreedAt) {
	public static Agreement create(boolean re100, boolean collection, boolean thirdParty, boolean groupRule,
			boolean marketing) {

		return Agreement.builder()
			.re100Interest(re100)
			.personalInfoCollectionAgreed(collection)
			.personalInfoThirdPartyAgreed(thirdParty)
			.groupRuleAgreed(groupRule)
			.marketingAgreed(marketing)
			.agreedAt(LocalDateTime.now())
			.build();
	}
}