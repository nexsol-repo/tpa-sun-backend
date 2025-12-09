package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.Agreement;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
public class AgreementInfoEmbeddable {

	private boolean re100Interest;

	private boolean personalInfoCollectionAgreed;

	private boolean personalInfoThirdPartyAgreed;

	private boolean groupRuleAgreed;

	private boolean marketingAgreed;

	private LocalDateTime agreedAt;

	public AgreementInfoEmbeddable(Agreement domain) {
		this.re100Interest = domain.re100Interest();
		this.personalInfoCollectionAgreed = domain.personalInfoCollectionAgreed();
		this.personalInfoThirdPartyAgreed = domain.personalInfoThirdPartyAgreed();
		this.groupRuleAgreed = domain.groupRuleAgreed();
		this.marketingAgreed = domain.marketingAgreed();
		this.agreedAt = domain.agreedAt();
	}

	public Agreement toDomain() {
		return Agreement.builder()
			.re100Interest(re100Interest)
			.personalInfoCollectionAgreed(personalInfoCollectionAgreed)
			.personalInfoThirdPartyAgreed(personalInfoThirdPartyAgreed)
			.groupRuleAgreed(groupRuleAgreed)
			.marketingAgreed(marketingAgreed)
			.agreedAt(agreedAt)
			.build();
	}

}
