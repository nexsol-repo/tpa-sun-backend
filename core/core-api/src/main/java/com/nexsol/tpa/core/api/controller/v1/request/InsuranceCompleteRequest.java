package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.DocumentFile;
import jakarta.validation.constraints.NotNull;

public record InsuranceCompleteRequest(@NotNull(message = "서명은 필수입니다.") DocumentRequest signature) {
	public DocumentFile toSignatureFile() {
		return signature.toDocumentFile();
	}
}
