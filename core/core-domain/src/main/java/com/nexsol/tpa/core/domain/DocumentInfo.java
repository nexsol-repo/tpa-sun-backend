package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record DocumentInfo(FileInfo businessLicense, FileInfo powerGenerationLicense, FileInfo preUseInspection,
		FileInfo supplyCertificate, FileInfo etc) {

	public static DocumentInfo toDocumentInfo(InsuranceDocument docs) {
		if (docs == null)
			return null;

		return DocumentInfo.builder()
			// InsuranceDocuments 도메인의 편의 메서드 활용
			.businessLicense(FileInfo.toFileInfo(docs.getBusinessLicense().orElse(null)))
			.powerGenerationLicense(FileInfo.toFileInfo(docs.getPowerGenLicense().orElse(null)))
			// 나머지 서류들에 대한 Getter가 도메인에 있다면 사용, 없다면 findByType 로직 활용
			// (여기서는 예시로 getPreUseInspection 등이 있다고 가정하거나, 직접 찾습니다)
			.preUseInspection(FileInfo.toFileInfo(
					docs.findAttachmentByType(com.nexsol.tpa.core.enums.InsuranceDocumentType.PRE_USE_INSPECTION)
						.orElse(null)))
			.supplyCertificate(FileInfo.toFileInfo(
					docs.findAttachmentByType(com.nexsol.tpa.core.enums.InsuranceDocumentType.SUPPLY_CERTIFICATE)
						.orElse(null)))
			.etc(FileInfo.toFileInfo(
					docs.findAttachmentByType(com.nexsol.tpa.core.enums.InsuranceDocumentType.ETC).orElse(null)))
			.build();
	}
}
