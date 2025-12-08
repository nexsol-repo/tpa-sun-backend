package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

@Builder
public record DocumentFile(String fileKey, String originalFileName, String extension, long size) {
	public DocumentFile {
		if (fileKey == null || fileKey.isBlank()) {
			throw new CoreException(CoreErrorType.FILE_UPLOAD_VALIDATION_KEY);
		}
	}
}
