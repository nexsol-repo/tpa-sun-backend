package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.AccidentReportDetail;
import com.nexsol.tpa.core.domain.FileInfo;
import com.nexsol.tpa.core.enums.AccidentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Builder
public record AccidentReportDetailResponse(Long reportId, String accidentNumber, AccidentStatus status,
		LocalDateTime reportedAt,

		InsuredInfoResponse insuredInfo,

		PlantInfoResponse plantInfo,

		AccidentInfoResponse accidentInfo,

		List<AttachmentResponse> attachments) {

	public static AccidentReportDetailResponse of(AccidentReportDetail detail, Function<String, String> urlGenerator) {
		List<AttachmentResponse> files = (detail.attachments() == null) ? List.of()
				: detail.attachments()
					.stream()
					.map(att -> new AttachmentResponse(att.type(), // 사고 서류 종류 (예: 현장사진)
							FileInfo.toFileInfo(att.file(), urlGenerator) // 위에서 만든 메서드 사용
					))
					.toList();

		return AccidentReportDetailResponse.builder()
			.reportId(detail.id())
			.accidentNumber(detail.accidentNumber())
			.status(detail.status())
			.reportedAt(detail.reportedAt())

			.insuredInfo(InsuredInfoResponse.builder()
				.companyCode(detail.insuredInfo().companyCode())
				.ceoName(detail.insuredInfo().ceoName())
				.ceoPhone(detail.insuredInfo().ceoPhone())
				.build())
			.plantInfo(PlantInfoResponse.builder()
				.plantName(detail.plantInfo().name())
				.plantAddress(detail.plantInfo().address())
				.capacity(detail.plantInfo().capacity())
				.area(detail.plantInfo().area())
				.build())
			.accidentInfo(AccidentInfoResponse.builder()
				.accidentType(detail.accidentInfo().accidentType())
				.accidentDate(detail.accidentInfo().accidentDate())
				.accidentPlace(detail.accidentInfo().accidentPlace())
				.damageDescription(detail.accidentInfo().damageDescription())
				.estimatedLossAmount(detail.accidentInfo().estimatedLossAmount())
				.accountBank(detail.accidentInfo().accountBank())
				.accountNumber(detail.accidentInfo().accountNumber())
				.accountHolder(detail.accidentInfo().accountHolder())
				.build())

			.attachments(files)
			.build();
	}

	@Builder
	public record InsuredInfoResponse(String companyCode, String ceoName, String ceoPhone) {
	}

	@Builder
	public record PlantInfoResponse(String plantName, String plantAddress, BigDecimal capacity, BigDecimal area) {
	}

	@Builder
	public record AccidentInfoResponse(String accidentType, LocalDateTime accidentDate, String accidentPlace,
			String damageDescription, Long estimatedLossAmount, String accountBank, String accountNumber,
			String accountHolder) {
	}

	public record AttachmentResponse(String type, FileInfo file) {
	}
}
