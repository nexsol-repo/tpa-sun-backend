package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.DocumentFile;
import com.nexsol.tpa.core.domain.InsuranceAttachment;
import com.nexsol.tpa.core.enums.InsuranceDocumentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "insurance_attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsuranceAttachmentEntity extends BaseEntity {

	@Column(nullable = false)
	private Long applicationId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InsuranceDocumentType type;

	private String fileKey;

	private String originalFileName;

	private String extension;

	private Long size;

	public static InsuranceAttachmentEntity fromDomain(InsuranceAttachment domain, Long applicationId) {
		InsuranceAttachmentEntity entity = new InsuranceAttachmentEntity();
		entity.applicationId = applicationId;
		entity.type = domain.type();

		if (domain.file() != null) {
			entity.fileKey = domain.file().fileKey();
			entity.originalFileName = domain.file().originalFileName();
			entity.extension = domain.file().extension();
			entity.size = domain.file().size();
		}
		return entity;
	}

	public InsuranceAttachment toDomain() {
		return InsuranceAttachment.builder()
			.type(this.type)
			.file(DocumentFile.builder() // DocumentFile 재조립
				.fileKey(this.fileKey)
				.originalFileName(this.originalFileName)
				.extension(this.extension)
				.size(this.size)
				.build())
			.build();
	}

}
