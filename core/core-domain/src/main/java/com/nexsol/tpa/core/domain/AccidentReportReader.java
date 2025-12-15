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

        String plantName = (app.plant() != null) ? app.plant().name() : "";
        String plantAddress = (app.plant() != null) ? app.plant().address() : "";


        return AccidentReportDetail.builder()
                .id(report.id())
                .userId(report.userId()) // 검증용
                .accidentNumber(report.accidentNumber())
                .status(report.status())
                .reportedAt(report.reportedAt())
                .accidentInfo(report.accidentInfo())
                .attachments(report.attachments())
                .applicationId(report.applicationId())
                .plantName(plantName)
                .plantAddress(plantAddress)
                .build();
    }

    public PageResult<AccidentReportSummary> readList(Long userId, SortPage sortPage) {

        PageResult<AccidentReport> reports = accidentReportRepository.findAllByUserId(userId, sortPage);


        List<Long> applicationIds = reports.getContent().stream()
                .map(AccidentReport::applicationId)
                .distinct()
                .toList();

        List<InsuranceApplication> applications = applicationReader.readAllByIds(applicationIds);


        Map<Long, String> plantNameMap = applications.stream()
                .collect(Collectors.toMap(
                        InsuranceApplication::id,
                        app -> (app.plant() != null) ? app.plant().name() : ""
                ));


        List<AccidentReportSummary> summaries = reports.getContent().stream()
                .map(report -> {
                    String name = plantNameMap.getOrDefault(report.applicationId(), "");

                    return AccidentReportSummary.builder()
                            .id(report.id())
                            .accidentNumber(report.accidentNumber())
                            .plantName(name)
                            .accidentType(report.accidentInfo().accidentType())
                            .accidentDate(report.accidentInfo().accidentDate())
                            .reportedAt(report.reportedAt())
                            .status(report.status())
                            .build();
                })
                .toList();

        return new PageResult<>(summaries, reports.getTotalElements(), reports.getTotalPages(), reports.getCurrentPage(), reports.hasNext());
    }
}
