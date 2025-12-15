package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccidentReportService {

	private final AccidentContractValidator contractValidator;

	private final AccidentReportReader reportReader;

	private final AccidentReportAppender reportAppender;

	private final AccidentNumberGenerator accidentNumberGenerator;


	public AccidentReport reportAccident(NewAccidentReport newReport) {

		contractValidator.validate(newReport.userId(), newReport.applicationId());

		AccidentReport report = newReport.toAccidentReport();

		String accidentNumber = accidentNumberGenerator.generate();
		report = report.assignNumber(accidentNumber);

		return reportAppender.append(report);
	}

	public AccidentReportDetail getDetail(Long userId, Long reportId){
		AccidentReportDetail detail = reportReader.readDetail(reportId);

		detail.validateOwner(userId);

		return detail;
	}
}
