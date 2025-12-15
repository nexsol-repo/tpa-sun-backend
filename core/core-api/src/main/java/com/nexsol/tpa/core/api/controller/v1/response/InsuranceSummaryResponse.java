package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.InsuranceApplication;
import lombok.Builder;

@Builder
public record InsuranceSummaryResponse(Long applicationId, String plantName) {
	public static InsuranceSummaryResponse of(InsuranceApplication app) {

		String name = (app.plant() != null) ? app.plant().name() : "";
		;

		return InsuranceSummaryResponse.builder().applicationId(app.id()).plantName(name).build();
	}
}