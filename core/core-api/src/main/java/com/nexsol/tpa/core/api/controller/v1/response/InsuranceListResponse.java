package com.nexsol.tpa.core.api.controller.v1.response;

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
		Integer nextStep, String division, LocalDate startDate, LocalDate endDate,

		// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",
		// timezone = "Asia/Seoul") // 포맷
		// 지정
		LocalDateTime paymentDate) {

	public static InsuranceListResponse toInsuranceListResponse(InsuranceApplication app) {
		// 1. 보험 시작일/종료일 계산
		LocalDate start = null;
		LocalDate end = null;

		// 가입 조건(Condition)이 입력된 경우에만 날짜 계산
		if (app.condition() != null && app.condition().startDate() != null) {
			start = app.condition().startDate();
			end = app.condition().endDate();
		}

		// 2. 결제일 (가입 완료 상태일 때만 노출, 임시로 updatedAt 사용)
		LocalDateTime paymentDate = (app.status() == InsuranceStatus.COMPLETED) ? app.updatedAt() : null;

		return InsuranceListResponse.builder()
			.applicationId(app.id())
			.applicationNumber(app.applicationNumber())
			.plantName(app.plant() != null ? app.plant().name() : null)
			.status(app.status())
			.division(displayDivision(end, app.status()))
			.nextStep(calculateNextStep(app))
			.applicantName(app.applicant() != null ? app.applicant().applicantName() : null)
			.totalPremium(app.quote() != null ? app.quote().totalPremium() : 0L)
			.startDate(start)
			.endDate(end)
			.paymentDate(paymentDate)
			.build();
	}

	public static List<InsuranceListResponse> of(List<InsuranceApplication> apps) {
		if (apps == null) {
			return List.of();
		}
		return apps.stream().map(InsuranceListResponse::toInsuranceListResponse).toList();
	}

	private static String displayDivision(LocalDate endDate, InsuranceStatus status) {
		if (status == InsuranceStatus.COMPLETED && endDate != null && LocalDate.now().isAfter(endDate)) {
			return "갱신";
		}
		return "신규";
	}

	private static int calculateNextStep(InsuranceApplication app) {
		if (app == null)
			return 1; // 청약서 자체가 없으면 1단계(약관동의)부터 시작

		if (app.status() == InsuranceStatus.COMPLETED)
			return 5; // 완료된 경우

		if (app.quote() != null)
			return 4; // 견적이 있으면 4단계(최종서명)로

		if (app.plant() != null && app.plant().name() != null)
			return 3; // 발전소 정보가 있으면 3단계(조건입력)로

		if (app.agreement() != null)
			return 2; // 약관 동의가 있으면 2단계(발전소입력)로

		return 1; // 그 외 초기 상태는 1단계
	}
}
