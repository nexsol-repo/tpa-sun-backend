package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.AccidentReport;
import com.nexsol.tpa.core.enums.AccidentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccidentReportListResponse(Long reportId, String accidentNumber, String insuredName, String plantName,
		String accidentType, String accidentPlace, LocalDateTime accidentDate, LocalDateTime reportedAt,
		AccidentStatus status) {

	public static AccidentReportListResponse of(AccidentReport report) {
		return AccidentReportListResponse.builder()
			.reportId(report.id())
			.accidentNumber(report.accidentNumber())
			.insuredName(report.insuredName())
			.plantName(report.plantName())
			.accidentPlace(report.accidentInfo().accidentPlace())
			.accidentType(report.accidentInfo().accidentType())
			.accidentDate(report.accidentInfo().accidentDate())
			.reportedAt(report.reportedAt())
			.status(report.status())
			.build();
	}
}
