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

	public int calculateNextStep() {
		// 1. 이미 가입 완료된 경우 -> 상세 페이지(5) 또는 별도 코드
		if (this.status == InsuranceStatus.COMPLETED) {
			return 5;
		}

		// 2. 견적(Quote)이 있다? -> 조건 입력(3단계)이 끝났다는 뜻 -> 서명(4단계)으로 이동
		if (this.quote != null) {
			return 4;
		}

		// 3. 발전소(Plant) 정보가 있다? -> 발전소 입력(2단계)이 끝났다는 뜻 -> 조건 입력(3단계)으로 이동
		// (단, Plant가 Embeddable로 바뀌면서 null이 아닐 수 있으므로 내부 필수값(name 등)으로 체크 권장)
		if (this.plant != null && this.plant.name() != null) {
			return 3;
		}

		// 4. 아무것도 없다 -> 이제 막 약관동의하고 생성됨 -> 발전소 입력(2단계)으로 이동
		return 2;
	}
}
