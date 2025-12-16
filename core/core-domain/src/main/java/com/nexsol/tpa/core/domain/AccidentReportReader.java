package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccidentReportReader {

	private final AccidentReportRepository accidentReportRepository;

	private final InsuranceApplicationReader applicationReader;

	public AccidentReportDetail readDetail(Long reportId) {

		AccidentReport report = accidentReportRepository.findById(reportId)
			.orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));

		InsuranceApplication app = applicationReader.read(report.applicationId());

		InsurancePlant plant = app.plant();
		Applicant applicant = app.applicant();

		AccidentPlant plantInfo = AccidentPlant.builder()
			.name(plant != null ? plant.name() : "")
			.address(plant != null ? plant.address() : "")
			.capacity(plant != null ? plant.capacity() : null)
			.area(plant != null ? plant.area() : null)
			.build();

		AccidentInsured insuredInfo = AccidentInsured.builder()
			.companyCode(applicant != null ? applicant.companyCode() : "")
			.ceoName(applicant != null ? applicant.ceoName() : "")
			.ceoPhone(applicant != null ? applicant.ceoPhoneNumber() : "")
			.build();

		return AccidentReportDetail.builder()
			.id(report.id())
			.accidentNumber(report.accidentNumber())
			.userId(report.userId())
			.status(report.status())
			.reportedAt(report.reportedAt())
			.accidentInfo(report.accidentInfo())
			.attachments(report.attachments())
			.plantInfo(plantInfo)
			.insuredInfo(insuredInfo)
			.build();

	}

	public PageResult<AccidentReport> readList(Long userId, SortPage sortPage) {
		return accidentReportRepository.findAllByUserId(userId, sortPage);

	}

}
