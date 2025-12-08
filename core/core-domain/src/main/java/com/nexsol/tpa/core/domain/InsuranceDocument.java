package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public record InsuranceDocument(List<InsuranceAttachment> attachments) {

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