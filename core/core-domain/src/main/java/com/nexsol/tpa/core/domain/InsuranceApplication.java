package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record InsuranceApplication(Long id, String applicationNumber, Long userId, InsuranceStatus status,
		Applicant applicant, Agreement agreement, InsurancePlant plant, JoinCondition condition,
		InsuranceDocument documents, PremiumQuote quote, LocalDateTime createdAt, LocalDateTime updatedAt

) {

	public static InsuranceApplication create(Long userId, String appNumber, Applicant applicant, Agreement agreement) {
		return InsuranceApplication.builder()
			.userId(userId)
			.applicationNumber(appNumber)
			.applicant(applicant)
			.agreement(agreement)
			.status(InsuranceStatus.PENDING)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public void validateOwner(Long currentUserId) {
		if (!this.userId.equals(currentUserId)) {
			throw new CoreException(CoreErrorType.INSURANCE_USER_UNAUTHORIZED);
		}
	}

	public InsuranceApplication complete() {
		if (this.plant == null || this.condition == null || this.quote == null) {
			throw new CoreException(CoreErrorType.INVALID_INPUT, "모든 가입 단계를 완료해야 합니다.");
		}
		return this.toBuilder().status(InsuranceStatus.COMPLETED).build();
	}
}
