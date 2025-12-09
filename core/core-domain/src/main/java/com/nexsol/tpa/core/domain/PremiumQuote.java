package com.nexsol.tpa.core.domain;

import lombok.Builder;

/**
 * @param mdPremium 재물 보험료
 * @param biPremium 휴지 보험료
 * @param glPremium 배상 보험료
 * @param totalPremium 총 보험료
 */
@Builder
public record PremiumQuote(Long mdPremium, Long biPremium, Long glPremium, Long totalPremium) {

	public static PremiumQuote empty() {
		return new PremiumQuote(0L, 0L, 0L, 0L);
	}
}
