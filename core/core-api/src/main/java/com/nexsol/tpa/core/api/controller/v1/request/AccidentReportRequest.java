package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.AccidentAttachment;
import com.nexsol.tpa.core.domain.NewAccidentReport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

public record AccidentReportRequest(@NotNull(message = "계약 ID는 필수입니다.") Long applicationId,

		@Valid @NotNull AccidentInfoRequest accidentInfo,

		@Valid List<AccidentAttachmentRequest> attachments) {

	public NewAccidentReport toNewAccidentReport(Long userId) {
		return NewAccidentReport.builder()
			.userId(userId)
			.applicationId(applicationId)
			.accidentInfo(accidentInfo.toAccidentInfo())
			.attachments(resolveAttachments())
			.build();
	}

	private List<AccidentAttachment> resolveAttachments() {
		if (attachments == null)
			return Collections.emptyList();
		return attachments.stream().map(AccidentAttachmentRequest::toAccidentAttachment).toList();
	}
}
