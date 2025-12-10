package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Builder
public record InsuranceDocument(List<InsuranceAttachment> attachments) {

	public InsuranceDocument {
		// Null-Safety: 리스트가 없으면 빈 리스트로 초기화
		if (attachments == null) {
			attachments = Collections.emptyList();
		}
	}

	public InsuranceDocument addSignature(DocumentFile signatureFile) {
		// 1. 서명 첨부 파일(Attachment) 개념 생성
		InsuranceAttachment signatureAttachment = InsuranceAttachment.builder()
			.type(InsuranceDocumentType.SIGNATURE)
			.file(signatureFile)
			.build();

		// 2. 기존 리스트 복사 (기존 리스트는 불변일 수 있으므로 수정 가능한 ArrayList로 복사)
		List<InsuranceAttachment> newAttachments = new ArrayList<>(this.attachments);

		// 3. (선택사항) 기존에 서명이 있다면 제거 (덮어쓰기 정책)
		newAttachments.removeIf(att -> att.type() == InsuranceDocumentType.SIGNATURE);

		// 4. 리스트에 추가
		newAttachments.add(signatureAttachment);

		// 5. 새로운 개념 객체 반환
		return new InsuranceDocument(Collections.unmodifiableList(newAttachments));
	}

	// [개념의 역할] 리스트를 "의미 있는 서류"로 변환해주는 메서드 제공
	public Optional<InsuranceAttachment> getBusinessLicense() {
		return findByType(InsuranceDocumentType.BUSINESS_LICENSE);
	}

	public Optional<InsuranceAttachment> getPowerGenLicense() {
		return findByType(InsuranceDocumentType.POWER_GEN_LICENSE);
	}

	// 필수 서류 확인 로직 등도 여기에 위치
	public boolean hasRequiredDocuments() {
		return getBusinessLicense().isPresent() && getPowerGenLicense().isPresent();
	}

	public Optional<InsuranceAttachment> findAttachmentByType(InsuranceDocumentType type) {
		if (attachments == null)
			return Optional.empty();
		return attachments.stream().filter(a -> a.type() == type).findFirst();
	}

	private Optional<InsuranceAttachment> findByType(InsuranceDocumentType type) {
		if (attachments == null)
			return Optional.empty();
		return attachments.stream().filter(a -> a.type() == type).findFirst();
	}

}