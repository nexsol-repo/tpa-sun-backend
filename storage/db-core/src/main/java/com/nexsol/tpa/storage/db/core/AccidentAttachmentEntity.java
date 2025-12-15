package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentAttachment;
import com.nexsol.tpa.core.domain.DocumentFile;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accident_attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccidentAttachmentEntity extends BaseEntity {

	private Long accidentReportId;

	private String attachmentType;

	private String fileKey;

	private String originalFileName;

	private String extension;

	private Long size;

	public static AccidentAttachmentEntity from(AccidentAttachment domain, Long reportId) {
		AccidentAttachmentEntity entity = new AccidentAttachmentEntity();
		entity.accidentReportId = reportId;
		entity.attachmentType = domain.type();

		if (domain.file() != null) {
			entity.fileKey = domain.file().fileKey();
			entity.originalFileName = domain.file().originalFileName();
			entity.extension = domain.file().extension();
			entity.size = domain.file().size();
		}
		return entity;
	}

	public AccidentAttachment toDomain() {
		return AccidentAttachment.builder()
			.type(this.attachmentType)
			.file(DocumentFile.builder()
				.fileKey(this.fileKey)
				.originalFileName(this.originalFileName)
				.extension(this.extension)
				.size(this.size)
				.build())
			.build();
	}

}
