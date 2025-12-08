package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.DocumentFile;

public record DocumentRequest(String key, String name, long size, String extension) {

	public DocumentFile toDocumentFile() {
		if (this.key == null)
			return null;

		return DocumentFile.builder()
			.fileKey(this.key)
			.originalFileName(this.name)
			.size(this.size)
			.extension(this.extension)
			.build();
	}
}
