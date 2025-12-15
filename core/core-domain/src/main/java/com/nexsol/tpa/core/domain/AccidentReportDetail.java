package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.AccidentStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AccidentReportDetail(
        Long id,
        Long userId,
        String accidentNumber,
        AccidentStatus status,
        LocalDateTime reportedAt,

        AccidentInfo accidentInfo,

        List<AccidentAttachment> attachments,

        Long applicationId,
        String plantName,
        String plantAddress
) {

    public void validateOwner(Long currentUserId) {
        if (!this.userId.equals(currentUserId)) {
            throw new CoreException(CoreErrorType.INSURANCE_USER_UNAUTHORIZED);
        }
    }
}
