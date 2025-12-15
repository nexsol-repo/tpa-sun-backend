package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.AccidentReport;
import com.nexsol.tpa.core.enums.AccidentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccidentReportResponse(Long reportId, Long applicationId, AccidentStatus status,
		LocalDateTime reportedAt) {
	public static AccidentReportResponse of(AccidentReport report) {
		return AccidentReportResponse.builder()
			.reportId(report.id())
			.applicationId(report.applicationId())
			.status(report.status())
			.reportedAt(report.reportedAt())
			.build();
	}
}
