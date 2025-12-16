package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccidentReportService {

	private final AccidentContractValidator contractValidator;

	private final AccidentReportReader reportReader;

	private final AccidentReportAppender reportAppender;

	private final AccidentNumberGenerator accidentNumberGenerator;

	private final InsuranceApplicationReader applicationReader;

	public AccidentReport reportAccident(NewAccidentReport newReport) {

		contractValidator.validate(newReport.userId(), newReport.applicationId());
		InsuranceApplication app = applicationReader.read(newReport.applicationId());

		AccidentReport report = newReport.toAccidentReport(app.plant().name(), app.applicant().ceoName(),
				app.applicant().ceoPhoneNumber());

		String accidentNumber = accidentNumberGenerator.generate();

		return reportAppender.append(report.assignNumber(accidentNumber));
	}

	public AccidentReportDetail getDetail(Long userId, Long reportId) {
		AccidentReportDetail detail = reportReader.readDetail(reportId);

		detail.validateOwner(userId);

		return detail;
	}

	public PageResult<AccidentReport> getList(Long userId, SortPage sortPage) {
		return reportReader.readList(userId, sortPage);
	}

}
