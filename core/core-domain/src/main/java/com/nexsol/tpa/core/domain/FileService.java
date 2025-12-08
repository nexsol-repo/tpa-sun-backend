package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

	private final FileStorageClient fileStorageClient;

	public DocumentFile uploadInsurance(InputStream inputStream, String originalFileName, long size,
			String contentType) {
		String extension = getExtension(originalFileName);

		if (!"pdf".equalsIgnoreCase(extension)) {
			throw new CoreException(CoreErrorType.FILE_UPLOAD_VALIDATION_CONTENT);
		}

		String objectKey = "insurance/" + UUID.randomUUID() + "." + extension;

		String savedKey = fileStorageClient.upload(inputStream, objectKey, size, contentType);

		return DocumentFile.builder()
			.fileKey(savedKey)
			.originalFileName(originalFileName)
			.extension(extension)
			.size(size)
			.build();
	}

	public DocumentFile uploadSignature(InputStream inputStream, String originalFileName, long size,
			String contentType) {
		String extension = getExtension(originalFileName);

		if (!isImageExtension(extension)) {
			throw new CoreException(CoreErrorType.FILE_UPLOAD_VALIDATION_IMAGE);
		}

		String objectKey = "signatures/" + UUID.randomUUID() + "." + extension;

		String savedKey = fileStorageClient.upload(inputStream, objectKey, size, contentType);

		return DocumentFile.builder()
			.fileKey(savedKey)
			.originalFileName(originalFileName)
			.extension(extension)
			.size(size)
			.build();
	}

	public void deleteFile(String fileKey) {
		if (fileKey == null || fileKey.isBlank()) {
			throw new CoreException(CoreErrorType.FILE_UPLOAD_NOT_FOUND_DATA);
		}

		fileStorageClient.delete(fileKey);
	}

	private String getExtension(String filename) {
		if (filename == null || !filename.contains("."))
			return "";
		return filename.substring(filename.lastIndexOf(".") + 1);
	}

	private boolean isImageExtension(String ext) {
		return "png".equalsIgnoreCase(ext) || "jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext);
	}

}
