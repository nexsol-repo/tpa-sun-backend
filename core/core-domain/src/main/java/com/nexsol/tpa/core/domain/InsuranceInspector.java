package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import org.springframework.stereotype.Component;

@Component
public class InsuranceInspector {

	public void inspectCondition(JoinCondition condition) {
		if (condition == null)
			return;

		boolean isEssInstalled = Boolean.TRUE.equals(condition.essInstalled());
		long pdAmount = condition.propertyDamageAmount() != null ? condition.propertyDamageAmount() : 0;
		long liabilityAmount = condition.liabilityAmount() != null ? condition.liabilityAmount() : 0;

		if (isEssInstalled && pdAmount >= 3_000_000_000L && liabilityAmount > 1_000_000_000L) {
			throw new CoreException(CoreErrorType.INSURANCE_MANUAL_CONSULTATION_REQUIRED);
		}
	}

	public void inspectDocuments(InsuranceDocument documents) {
		if (documents.attachments() != null) {
			documents.attachments().forEach(att -> validatePdf(att.file()));
		}
	}

	private void validatePdf(DocumentFile file) {
		if (file != null && !"pdf".equalsIgnoreCase(file.extension())) {
			throw new CoreException(CoreErrorType.FILE_UPLOAD_VALIDATION_CONTENT);
		}
	}

}
