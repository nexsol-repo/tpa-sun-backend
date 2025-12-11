package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record FileInfo(String key, String originalFileName, long size, String extension, String url) {
    public static FileInfo toFileInfo(InsuranceAttachment attachment, java.util.function.Function<String, String> urlGenerator) {
        if (attachment == null || attachment.file() == null) return null;

        DocumentFile file = attachment.file();
        String presignedUrl = null;

        // 키가 존재하면 URL 생성 함수 실행
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
