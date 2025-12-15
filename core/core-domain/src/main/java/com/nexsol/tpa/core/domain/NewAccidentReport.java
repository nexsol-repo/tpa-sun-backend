package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record NewAccidentReport(Long userId, Long applicationId, AccidentInfo accidentInfo,
		List<AccidentAttachment> attachments) {
	public AccidentReport toAccidentReport() {
		return AccidentReport.create(userId, applicationId, accidentInfo, attachments);
	}
}