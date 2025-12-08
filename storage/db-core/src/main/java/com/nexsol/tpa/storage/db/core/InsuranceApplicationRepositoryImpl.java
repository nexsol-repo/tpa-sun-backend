package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsuranceApplication;
import com.nexsol.tpa.core.domain.InsuranceApplicationRepository;
import com.nexsol.tpa.core.domain.InsuranceDocument;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InsuranceApplicationRepositoryImpl implements InsuranceApplicationRepository {

	private final InsuranceApplicationJpaRepository applicationJpaRepository;

	private final InsuranceAttachmentJpaRepository attachmentJpaRepository;

	@Override
	public InsuranceApplication save(InsuranceApplication application) {
		InsuranceApplicationEntity entity;
		if (application.id() == null) {
			entity = InsuranceApplicationEntity.fromDomain(application);

		}
		else {
			entity = applicationJpaRepository.findById(application.id())
				.orElseGet(() -> InsuranceApplicationEntity.fromDomain(application));
			entity.update(application);
		}

		InsuranceApplicationEntity savedEntity = applicationJpaRepository.save(entity);

		if (application.documents() != null) {
			saveAttachments(savedEntity.getId(), application.documents());
		}

		return toInsuranceApplication(savedEntity);

	}

	@Override
	public Optional<InsuranceApplication> findById(Long id) {
		return applicationJpaRepository.findById(id).map(this::toInsuranceApplication);
	}

	@Override
	public Optional<InsuranceApplication> findByApplicationNumber(String applicationNumber) {
		return applicationJpaRepository.findByApplicationNumber(applicationNumber).map(this::toInsuranceApplication);
	}

	@Override
	public Optional<InsuranceApplication> findWritingApplication(Long userId) {
		return applicationJpaRepository.findByUserIdAndStatus(userId, InsuranceStatus.PENDING)
			.map(this::toInsuranceApplication);
	}

	private InsuranceApplication toInsuranceApplication(InsuranceApplicationEntity entity) {
		List<InsuranceAttachmentEntity> attachments = attachmentJpaRepository.findByApplicationId(entity.getId());

		return entity.toDomain(attachments);
	}

	private void saveAttachments(Long applicationId, InsuranceDocument documents) {
		attachmentJpaRepository.deleteByApplicationId(applicationId);

		if (documents.attachments() == null || documents.attachments().isEmpty()) {
			return;
		}

		// 2. 도메인 객체를 엔티티로 변환하여 저장
		List<InsuranceAttachmentEntity> newAttachments = documents.attachments()
			.stream()
			.map(att -> InsuranceAttachmentEntity.fromDomain(att, applicationId))
			.toList();

		attachmentJpaRepository.saveAll(newAttachments);
	}

}
