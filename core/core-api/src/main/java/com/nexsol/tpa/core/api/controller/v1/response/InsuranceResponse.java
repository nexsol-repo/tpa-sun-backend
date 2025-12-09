package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import lombok.Builder;

@Builder
public record InsuranceResponse(Long id, String applicationNumber, InsuranceStatus status, Applicant applicantInfo, // 신청자
																													// 정보
		Agreement agreementInfo, // 약관 동의 정보
		InsurancePlant plantInfo, // 발전소 정보
		JoinCondition conditionInfo, // 가입 조건 (사고, 질권 포함)
		PremiumQuote coverageInfo, // 보험료/견적 정보 (Quote)
		DocumentInfo documentInfo

) {
	public static InsuranceResponse of(InsuranceApplication app) {
		return InsuranceResponse.builder()
			.id(app.id())
			.applicationNumber(app.applicationNumber())
			.status(app.status())
			.applicantInfo(app.applicant())
			.agreementInfo(app.agreement())
			.plantInfo(app.plant())
			.conditionInfo(app.condition())
			.coverageInfo(app.quote())
			.documentInfo(DocumentInfo.toDocumentInfo(app.documents()))
			.build();
	}

}
