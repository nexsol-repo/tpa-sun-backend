package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.DocumentFile;
import lombok.Builder;

@Builder
public record FileResponse(String key, String originalFileName, long size, String extension) {

	public static FileResponse of(DocumentFile file) {
		return FileResponse.builder()
			.key(file.fileKey())
			.originalFileName(file.originalFileName())
			.size(file.size())
			.extension(file.extension())
			.build();
	}
}
