package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.PremiumQuote;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class QuoteEmbeddable {

	private Long mdPremium;

	private Long biPremium;

	private Long glPremium;

	private Long totalPremium;

	public QuoteEmbeddable(PremiumQuote domain) {
		this.mdPremium = domain.mdPremium();
		this.biPremium = domain.biPremium();
		this.glPremium = domain.glPremium();
		this.totalPremium = domain.totalPremium();
	}

	public PremiumQuote toDomain() {
		return PremiumQuote.builder()
			.mdPremium(mdPremium)
			.biPremium(biPremium)
			.glPremium(glPremium)
			.totalPremium(totalPremium)
			.build();
	}

}
