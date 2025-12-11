package com.nexsol.tpa.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nexsol.tpa.core.domain.InsuranceApplication;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InsuranceListResponse(Long applicationId, String applicationNumber, // 청약 번호
		String plantName, // 발전소명
		InsuranceStatus status, // 상태 (작성중, 가입완료 등)
		String applicantName, // 신청자명
		Long totalPremium, // 납입 보험료
		Integer nextStep,

		LocalDate startDate,

		LocalDate endDate,

		// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",
		// timezone = "Asia/Seoul") // 포맷
		// 지정
		LocalDateTime paymentDate) {

	public static InsuranceListResponse of(InsuranceApplication app) {
		// 1. 보험 시작일/종료일 계산
		LocalDate start = null;
		LocalDate end = null;

		// 가입 조건(Condition)이 입력된 경우에만 날짜 계산
		if (app.condition() != null && app.condition().startDate() != null) {
			start = app.condition().startDate();
			end = start.plusYears(1); // 가입일 기준 1년
		}

		// 2. 결제일 (가입 완료 상태일 때만 노출, 임시로 updatedAt 사용)
		LocalDateTime paymentDate = (app.status() == InsuranceStatus.COMPLETED) ? app.updatedAt() : null;

		return InsuranceListResponse.builder()
			.applicationId(app.id())
			.applicationNumber(app.applicationNumber())
			.plantName(app.plant() != null ? app.plant().name() : null)
			.status(app.status())
			.nextStep(app.calculateNextStep())
			.applicantName(app.applicant() != null ? app.applicant().applicantName() : null)
			.totalPremium(app.quote() != null ? app.quote().totalPremium() : 0L)
			.startDate(start)
			.endDate(end)
			.paymentDate(paymentDate)
			.build();
	}

	public static List<InsuranceListResponse> from(List<InsuranceApplication> apps) {
		if (apps == null) {
			return List.of();
		}
		return apps.stream().map(InsuranceListResponse::of).toList();
	}
}
