package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.AccidentAttachment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccidentAttachmentRequest(
        @NotBlank(message = "서류 종류는 필수입니다.")
        String type,

        @NotNull
        DocumentRequest file
) {
    public AccidentAttachment toAccidentAttachment() {
        return AccidentAttachment.builder()
                .type(type)
                .file(file.toDocumentFile())
                .build();
    }
}
