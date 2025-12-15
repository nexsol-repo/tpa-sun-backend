package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccidentReportAppender {

	private final AccidentReportRepository accidentReportRepository;

	public AccidentReport append(AccidentReport report) {
		return accidentReportRepository.save(report);
	}

}
