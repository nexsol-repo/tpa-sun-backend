package com.nexsol.tpa.core.domain;

public record FileInfo(String key, String originalFileName, long size, String extension) {
	public static FileInfo toFileInfo(InsuranceAttachment attachment) {
		if (attachment == null || attachment.file() == null)
			return null;
		DocumentFile file = attachment.file();
		return new FileInfo(file.fileKey(), file.originalFileName(), file.size(), file.extension());
	}

}
