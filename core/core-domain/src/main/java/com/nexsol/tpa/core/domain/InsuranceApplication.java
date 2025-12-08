package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
public record InsuranceApplication(Long id, String applicationNumber, Long userId, ApplicantInfo applicantInfo,
		InsurancePlant plantInfo, InsuranceCondition condition, InsuranceCoverage coverage, InsuranceDocument documents,
		AgreementInfo agreementInfo, InsuranceStatus status, LocalDateTime createdAt, LocalDateTime updatedAt

) {

	public static InsuranceApplication create(Long userId, String applicationNumber, ApplicantInfo applicantInfo,
			AgreementInfo agreementInfo) {
		return InsuranceApplication.builder()
			.userId(userId)
			.applicationNumber(applicationNumber)
			.applicantInfo(applicantInfo)
			.agreementInfo(agreementInfo)
			.status(InsuranceStatus.PENDING)
			.build();
	}

	public InsuranceApplication updatePlantInfo(InsurancePlant newPlantInfo) {
		return InsuranceApplication.builder()
			.id(this.id)
			.applicationNumber(this.applicationNumber)
			.userId(this.userId)
			.applicantInfo(this.applicantInfo)
			.plantInfo(newPlantInfo) // 업데이트
			.condition(this.condition)
			.coverage(this.coverage)
			.documents(this.documents)
			.status(this.status)
			.createdAt(this.createdAt)
			.build();
	}

	// 3단계: 가입 조건 입력
	public InsuranceApplication updateConditionAndDocument(InsuranceCondition newCondition,
			InsuranceDocument newDocuments) {
		return InsuranceApplication.builder()
			.id(this.id)
			.applicationNumber(this.applicationNumber)
			.userId(this.userId)
			.applicantInfo(this.applicantInfo)
			.plantInfo(this.plantInfo)
			.condition(newCondition)
			.coverage(this.coverage)
			.documents(newDocuments)
			.status(this.status)
			.createdAt(this.createdAt)
			.build();
	}

	// 4단계: 보험료 산출 결과 적용
	public InsuranceApplication updateCoverage(InsuranceCoverage newCoverage) {
		return InsuranceApplication.builder()
			.id(this.id)
			.applicationNumber(this.applicationNumber)
			.userId(this.userId)
			.applicantInfo(this.applicantInfo)
			.plantInfo(this.plantInfo)
			.condition(this.condition)
			.coverage(newCoverage)
			.documents(this.documents)
			.status(this.status)
			.createdAt(this.createdAt)
			.build();
	}

	public InsuranceApplication signAndComplete(DocumentFile signatureFile) {
		InsuranceAttachment signatureAttachment = InsuranceAttachment.builder()
			.type(InsuranceDocumentType.SIGNATURE)
			.file(signatureFile)
			.build();

		List<InsuranceAttachment> newAttachments = new ArrayList<>();
		if (this.documents != null && this.documents.attachments() != null) {
			newAttachments.addAll(this.documents.attachments());
		}
		newAttachments.add(signatureAttachment);

		InsuranceDocument newDocs = InsuranceDocument.builder().attachments(newAttachments).build();

		return InsuranceApplication.builder()
			.id(this.id)
			.applicationNumber(this.applicationNumber)
			.userId(this.userId)
			.applicantInfo(this.applicantInfo)
			.plantInfo(this.plantInfo)
			.condition(this.condition)
			.coverage(this.coverage)
			.documents(newDocs) // 문서 업데이트 (서명 포함)
			.status(InsuranceStatus.COMPLETED) // 상태 변경
			.createdAt(this.createdAt)
			.build();
	}

	public void validateOwner(Long currentUserId) {
		if (!this.userId.equals(currentUserId)) {
			throw new CoreException(CoreErrorType.INSURANCE_USER_UNAUTHORIZED);
		}
	}
}
