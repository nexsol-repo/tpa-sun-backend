package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.AccidentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccidentReportSummary(
        Long id,
        String accidentNumber,
        String plantName,
        String accidentType,
        LocalDateTime accidentDate,
        LocalDateTime reportedAt,
        AccidentStatus status
) {

}