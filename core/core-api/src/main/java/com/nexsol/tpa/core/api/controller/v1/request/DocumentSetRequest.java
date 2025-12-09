package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.InsuranceAttachment;
import com.nexsol.tpa.core.domain.InsuranceDocument;
import com.nexsol.tpa.core.enums.InsuranceDocumentType;

import java.util.ArrayList;
import java.util.List;

public record DocumentSetRequest(DocumentRequest businessLicense, DocumentRequest powerGenerationLicense,
		DocumentRequest preUseInspection, DocumentRequest supplyCertificate, DocumentRequest etc) {

	public InsuranceDocument toDocument() {
		List<InsuranceAttachment> attachments = new ArrayList<>();

		// 각 문서를 타입에 맞춰 리스트에 추가
		addAttachment(attachments, businessLicense, InsuranceDocumentType.BUSINESS_LICENSE);
		addAttachment(attachments, powerGenerationLicense, InsuranceDocumentType.POWER_GEN_LICENSE);
		addAttachment(attachments, preUseInspection, InsuranceDocumentType.PRE_USE_INSPECTION);
		addAttachment(attachments, supplyCertificate, InsuranceDocumentType.SUPPLY_CERTIFICATE);
		addAttachment(attachments, etc, InsuranceDocumentType.ETC);

		return InsuranceDocument.builder().attachments(attachments).build();
	}

	private void addAttachment(List<InsuranceAttachment> list, DocumentRequest req, InsuranceDocumentType type) {
		if (req != null && req.key() != null) {

			list.add(InsuranceAttachment.builder().type(type).file(req.toDocumentFile()).build());
		}
	}

}