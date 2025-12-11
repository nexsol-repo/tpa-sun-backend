package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import lombok.Builder;

import java.util.function.Function;

@Builder
public record InsuranceResponse(Long id, String applicationNumber, InsuranceStatus status, Applicant applicantInfo,

                                Agreement agreementInfo,
                                InsurancePlant plantInfo,
                                JoinCondition conditionInfo,
                                PremiumQuote coverageInfo,
                                DocumentInfo documentInfo

) {
    public static InsuranceResponse of(InsuranceApplication app) {
        return of(app, null);
    }

    public static InsuranceResponse of(InsuranceApplication app, Function<String, String> urlGenerator) {
        return InsuranceResponse.builder()
                .id(app.id())
                .applicationNumber(app.applicationNumber())
                .status(app.status())
                .applicantInfo(app.applicant())
                .agreementInfo(app.agreement())
                .plantInfo(app.plant())
                .conditionInfo(app.condition())
                .coverageInfo(app.quote())
                .documentInfo(DocumentInfo.toDocumentInfo(app.documents(), urlGenerator))
                .build();
    }

}
