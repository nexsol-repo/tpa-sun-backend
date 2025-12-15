package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccidentReportService {

	private final AccidentContractValidator contractValidator;

	private final AccidentReportAppender reportAppender;

	private final AccidentNumberGenerator accidentNumberGenerator;

	public AccidentReport reportAccident(NewAccidentReport newReport) {

		contractValidator.validate(newReport.userId(), newReport.applicationId());

		AccidentReport report = newReport.toAccidentReport();

		String accidentNumber = accidentNumberGenerator.generate();
		report = report.assignNumber(accidentNumber);

		return reportAppender.append(report);
	}

}
