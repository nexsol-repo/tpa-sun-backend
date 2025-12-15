package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record AccidentAttachment(String type, DocumentFile file) {
}
