package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.DocumentInfo;
import com.nexsol.tpa.core.domain.InsuranceApplication;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import lombok.Builder;

@Builder
public record InsuranceResponse(Long id, String applicationNumber, InsuranceStatus status, Object applicantInfo,
		Object agreementInfo, Object plantInfo, Object conditionInfo, Object coverageInfo, DocumentInfo documentInfo

) {
	public static InsuranceResponse of(InsuranceApplication app) {
		return InsuranceResponse.builder()
			.id(app.id())
			.applicationNumber(app.applicationNumber())
			.status(app.status())
			.applicantInfo(app.applicantInfo())
			.agreementInfo(app.agreementInfo())
			.plantInfo(app.plantInfo())
			.conditionInfo(app.condition())
			.coverageInfo(app.coverage())
			.documentInfo(DocumentInfo.toDocumentInfo(app.documents()))
			.build();
	}

}
