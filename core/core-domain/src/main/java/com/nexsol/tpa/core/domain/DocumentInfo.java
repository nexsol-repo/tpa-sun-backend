package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import lombok.Builder;

import java.util.function.Function;

@Builder
public record DocumentInfo(FileInfo businessLicense, FileInfo powerGenerationLicense, FileInfo preUseInspection,
		FileInfo supplyCertificate, FileInfo etc, FileInfo signature) {

	public static DocumentInfo toDocumentInfo(InsuranceDocument docs, Function<String, String> urlGenerator) {
		if (docs == null)
			return null;

		return DocumentInfo.builder()
			.businessLicense(FileInfo.toFileInfo(docs.getBusinessLicense().orElse(null), urlGenerator))
			.powerGenerationLicense(FileInfo.toFileInfo(docs.getPowerGenLicense().orElse(null), urlGenerator))
			.preUseInspection(FileInfo.toFileInfo(
					docs.findAttachmentByType(InsuranceDocumentType.PRE_USE_INSPECTION).orElse(null), urlGenerator))
			.supplyCertificate(FileInfo.toFileInfo(
					docs.findAttachmentByType(InsuranceDocumentType.SUPPLY_CERTIFICATE).orElse(null), urlGenerator))
			.etc(FileInfo.toFileInfo(docs.findAttachmentByType(InsuranceDocumentType.ETC).orElse(null), urlGenerator))
			.signature(FileInfo.toFileInfo(docs.findAttachmentByType(InsuranceDocumentType.SIGNATURE).orElse(null),
					urlGenerator))
			.build();
	}
}
