package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.AccidentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record AccidentReport(Long id, String accidentNumber, Long userId, Long applicationId, AccidentInfo accidentInfo,
		List<AccidentAttachment> attachments, AccidentStatus status, LocalDateTime reportedAt) {

	public static AccidentReport create(Long userId, Long applicationId, AccidentInfo info,
			List<AccidentAttachment> attachments) {
		return AccidentReport.builder()
			.userId(userId)
			.applicationId(applicationId)
			.accidentInfo(info)
			.attachments(attachments)
			.status(AccidentStatus.RECEIVED) // 초기 상태: 접수 완료
			.reportedAt(LocalDateTime.now())
			.build();
	}

	public AccidentReport assignNumber(String number) {
		return this.toBuilder().accidentNumber(number).build();
	}
}
