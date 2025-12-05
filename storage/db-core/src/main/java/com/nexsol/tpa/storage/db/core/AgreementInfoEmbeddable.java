package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AgreementInfo;
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

    public AgreementInfoEmbeddable(AgreementInfo domain) {
        this.re100Interest = domain.re100Interest();
        this.personalInfoCollectionAgreed = domain.personalInfoCollectionAgreed();
        this.personalInfoThirdPartyAgreed = domain.personalInfoThirdPartyAgreed();
        this.groupRuleAgreed = domain.groupRuleAgreed();
        this.marketingAgreed = domain.marketingAgreed();
        this.agreedAt = domain.agreedAt();
    }

    public AgreementInfo toDomain() {
        return AgreementInfo.builder()
                .re100Interest(re100Interest)
                .personalInfoCollectionAgreed(personalInfoCollectionAgreed)
                .personalInfoThirdPartyAgreed(personalInfoThirdPartyAgreed)
                .groupRuleAgreed(groupRuleAgreed)
                .marketingAgreed(marketingAgreed)
                .agreedAt(agreedAt)
                .build();
    }
}
