package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.util.function.Function;

@Builder
public record FileInfo(String key, String originalFileName, long size, String extension, String url) {
	public static FileInfo toFileInfo(InsuranceAttachment attachment, Function<String, String> urlGenerator) {
		if (attachment == null)
			return null;
		return toFileInfo(attachment.file(), urlGenerator);
	}

	// [신규 추가] DocumentFile을 직접 변환하는 공용 메서드
	public static FileInfo toFileInfo(DocumentFile file, Function<String, String> urlGenerator) {
		if (file == null)
			return null;

		String presignedUrl = null;
		if (file.fileKey() != null && urlGenerator != null) {
			presignedUrl = urlGenerator.apply(file.fileKey());
		}

		return FileInfo.builder()
			.key(file.fileKey())
			.originalFileName(file.originalFileName())
			.size(file.size())
			.extension(file.extension())
			.url(presignedUrl)
			.build();
	}

}
