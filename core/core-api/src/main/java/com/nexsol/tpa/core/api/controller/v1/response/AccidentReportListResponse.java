package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.enums.AccidentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccidentListResponse(
        Long reportId,
        String accidentNumber,
        String plantName,
        String accidentType,
        LocalDateTime accidentDate,
        LocalDateTime reportedAt,
        AccidentStatus status
) {
}
