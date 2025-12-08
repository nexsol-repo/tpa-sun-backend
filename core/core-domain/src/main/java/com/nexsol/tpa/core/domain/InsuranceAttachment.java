package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import lombok.Builder;

@Builder
public record InsuranceAttachment(InsuranceDocumentType type, DocumentFile file) {
}
