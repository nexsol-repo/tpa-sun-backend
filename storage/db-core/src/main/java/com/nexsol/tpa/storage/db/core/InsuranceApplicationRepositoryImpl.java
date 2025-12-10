package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InsuranceApplicationRepositoryImpl implements InsuranceApplicationRepository {

	private final InsuranceApplicationJpaRepository applicationJpaRepository;

	private final InsuranceAttachmentJpaRepository attachmentJpaRepository;

	private final InsurancePlantJpaRepository plantJpaRepository;

	private final InsuranceConditionJpaRepository conditionJpaRepository;

	private final AccidentHistoryJpaRepository accidentHistoryJpaRepository;

	@Override
	@Transactional
	public InsuranceApplication save(InsuranceApplication application) {
		InsuranceApplicationEntity insuranceApplicationEntity = application.id() == null
				? InsuranceApplicationEntity.fromDomain(application)
				: applicationJpaRepository.findById(application.id())
					.orElseGet(() -> InsuranceApplicationEntity.fromDomain(application));

		insuranceApplicationEntity.update(application); // 상태, 견적 등 업데이트
		InsuranceApplicationEntity savedApplicationEntity = applicationJpaRepository.save(insuranceApplicationEntity);
		Long applicationId = savedApplicationEntity.getId();

		// 2. [Plant] 발전소 정보 저장 (UPSERT)
		if (application.plant() != null) {
			InsurancePlantEntity insurancePlantEntity = plantJpaRepository.findByApplicationId(applicationId)
				.orElseGet(() -> InsurancePlantEntity.fromDomain(application.plant(), applicationId));

			insurancePlantEntity.update(application.plant());
			plantJpaRepository.save(insurancePlantEntity);
		}

		// 3. [Condition] 가입 조건 저장 (UPSERT)
		if (application.condition() != null) {
			InsuranceConditionEntity insuranceConditionEntity = conditionJpaRepository
				.findByApplicationId(applicationId)
				.orElseGet(() -> InsuranceConditionEntity.fromDomain(application.condition(), applicationId));

			insuranceConditionEntity.update(application.condition());
			conditionJpaRepository.save(insuranceConditionEntity);

			// 3-1. [Accident] 사고 이력 저장 (Full Replace 전략: 삭제 후 재저장)
			accidentHistoryJpaRepository.deleteByApplicationId(applicationId);

			if (application.condition().hasAccidents()) {
				List<AccidentHistoryEntity> accidentHistoryEntities = application.condition()
					.accidents()
					.stream()
					.map(accident -> AccidentHistoryEntity.fromDomain(accident, applicationId))
					.toList();
				accidentHistoryJpaRepository.saveAll(accidentHistoryEntities);
			}
		}

		// 4. [Documents] 첨부 파일 저장
		if (application.documents() != null) {
			saveAttachments(applicationId, application.documents());
		}

		// 저장 후 재조회하여 완전한 상태 반환
		return findById(applicationId).orElseThrow();

	}

	@Override
	@Transactional
	public Optional<InsuranceApplication> findById(Long id) {
		Optional<InsuranceApplicationEntity> appEntity = applicationJpaRepository.findById(id);

		if (appEntity.isEmpty()) {
			return Optional.empty();
		}

		InsuranceApplicationEntity insuranceApplicationEntity = appEntity.get();

		InsurancePlant insurancePlant = plantJpaRepository.findByApplicationId(id)
			.map(InsurancePlantEntity::toDomain)
			.orElse(null);

		JoinCondition joinCondition = null;
		Optional<InsuranceConditionEntity> conditionEntityOptional = conditionJpaRepository.findByApplicationId(id);

		if (conditionEntityOptional.isPresent()) {
			List<Accident> accidents = accidentHistoryJpaRepository.findByApplicationId(id)
				.stream()
				.map(AccidentHistoryEntity::toDomain)
				.toList();
			joinCondition = conditionEntityOptional.get().toDomain(accidents);
		}
		InsuranceDocument insuranceDocument = loadDocuments(id);

		return Optional.of(insuranceApplicationEntity.toDomain(insurancePlant, joinCondition, insuranceDocument));
	}

	@Override
	public Optional<InsuranceApplication> findByApplicationNumber(String applicationNumber) {
		return applicationJpaRepository.findByApplicationNumber(applicationNumber)
			.flatMap(entity -> findById(entity.getId()));
	}

	@Override
	public Optional<InsuranceApplication> findWritingApplication(Long userId) {
		return applicationJpaRepository.findByUserIdAndStatus(userId, InsuranceStatus.PENDING)
			.flatMap(entity -> findById(entity.getId()));
	}

	private void saveAttachments(Long applicationId, InsuranceDocument documents) {
		attachmentJpaRepository.deleteByApplicationId(applicationId);

		if (documents.attachments() == null || documents.attachments().isEmpty()) {
			return;
		}

		List<InsuranceAttachmentEntity> attachmentEntities = documents.attachments()
			.stream()
			.map(attachment -> InsuranceAttachmentEntity.fromDomain(attachment, applicationId))
			.toList();

		attachmentJpaRepository.saveAll(attachmentEntities);
	}

	private InsuranceDocument loadDocuments(Long applicationId) {
		List<InsuranceAttachment> attachments = attachmentJpaRepository.findByApplicationId(applicationId)
			.stream()
			.map(InsuranceAttachmentEntity::toDomain)
			.toList();

		return InsuranceDocument.builder().attachments(attachments).build();
	}

}
