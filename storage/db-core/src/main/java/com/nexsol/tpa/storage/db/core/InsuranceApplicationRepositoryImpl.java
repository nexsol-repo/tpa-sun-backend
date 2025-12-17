package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InsuranceApplicationRepositoryImpl implements InsuranceApplicationRepository {

	private final InsuranceApplicationJpaRepository applicationJpaRepository;

	private final InsuranceAttachmentJpaRepository attachmentJpaRepository;

	private final InsuranceConditionJpaRepository conditionJpaRepository;

	private final AccidentHistoryJpaRepository accidentHistoryJpaRepository;

	@Override
	@Transactional
	public InsuranceApplication save(InsuranceApplication application) {
		InsuranceApplicationEntity entity = application.id() == null
				? InsuranceApplicationEntity.fromDomain(application)
				: applicationJpaRepository.findById(application.id())
					.orElseGet(() -> InsuranceApplicationEntity.fromDomain(application));

		entity.update(application);

		InsuranceApplicationEntity savedEntity = applicationJpaRepository.save(entity);
		Long applicationId = savedEntity.getId();

		// [Condition] 가입 조건 저장 (UPSERT)
		if (application.condition() != null) {
			InsuranceConditionEntity insuranceConditionEntity = conditionJpaRepository
				.findByApplicationId(applicationId)
				.orElseGet(() -> InsuranceConditionEntity.fromDomain(application.condition(), applicationId));

			insuranceConditionEntity.update(application.condition());
			conditionJpaRepository.save(insuranceConditionEntity);

			// [Accident] 사고 이력 저장 (Full Replace 전략: 삭제 후 재저장)
			accidentHistoryJpaRepository.deleteByApplicationId(applicationId);

			if (application.condition().hasAccident()) {
				Accident accident = application.condition().accident();
				AccidentHistoryEntity historyEntity = AccidentHistoryEntity.fromDomain(accident, applicationId);
				accidentHistoryJpaRepository.save(historyEntity);
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

		InsuranceCondition insuranceCondition = null;
		Optional<InsuranceConditionEntity> conditionEntityOptional = conditionJpaRepository.findByApplicationId(id);

		if (conditionEntityOptional.isPresent()) {
			Accident accident = accidentHistoryJpaRepository.findByApplicationId(id)
				.map(AccidentHistoryEntity::toDomain)
				.orElse(null);

			insuranceCondition = conditionEntityOptional.get().toDomain(accident);
		}
		InsuranceDocument insuranceDocument = loadDocuments(id);

		return Optional.of(insuranceApplicationEntity.toDomain(insuranceCondition, insuranceDocument));
	}

	@Override
	public List<InsuranceApplication> findAllById(List<Long> ids) {
		return applicationJpaRepository.findAllById(ids).stream().map(entity -> entity.toDomain(null, null)).toList();
	}

	@Override
	public PageResult<InsuranceApplication> findAllByUserId(Long userId, SortPage sortPage) {
		Pageable pageable = toJpaPageable(sortPage);

		Page<InsuranceApplicationEntity> entityPage = applicationJpaRepository.findByUserId(userId, pageable);

		List<Long> applicationIds = entityPage.getContent().stream().map(InsuranceApplicationEntity::getId).toList();

		Map<Long, InsuranceConditionEntity> conditionMap = conditionJpaRepository
			.findAllByApplicationIdIn(applicationIds)
			.stream()
			.collect(Collectors.toMap(InsuranceConditionEntity::getApplicationId, Function.identity()));

		List<InsuranceApplication> content = entityPage.getContent().stream().map(entity -> {
			InsuranceConditionEntity conditionEntity = conditionMap.get(entity.getId());
			// ConditionEntity가 있으면 도메인으로 변환, 없으면 null
			// (리스트이므로 Accident 정보까지 깊게 가져올 필요가 없다면 accident는 null 처리)
			InsuranceCondition condition = (conditionEntity != null) ? conditionEntity.toDomain(null) : null;

			return entity.toDomain(condition, null);
		}).toList();

		return new PageResult<>(content, entityPage.getTotalElements(), entityPage.getTotalPages(),
				entityPage.getNumber(), entityPage.hasNext());
	}

	@Override
	public List<InsuranceApplication> findAllByUserIdAndStatus(Long userId, InsuranceStatus status) {
		List<InsuranceApplicationEntity> entities = applicationJpaRepository.findAllByUserIdAndInsuranceStatus(userId,
				status);

		return entities.stream().map(entity -> entity.toDomain(null, null)).toList();
	}

	private Pageable toJpaPageable(SortPage sortPage) {
		if (sortPage.sort() == null) {
			return PageRequest.of(sortPage.page(), sortPage.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
		}

		SortPage.Sort sort = sortPage.sort();
		Sort.Direction direction = sort.direction().isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC;
		String property = sort.property();

		String entityProperty = switch (property) {

			case "plantName" -> "plantInfo.name";
			case "status" -> "insuranceStatus";
			default -> "createdAt";
		};

		return PageRequest.of(sortPage.page(), sortPage.size(), Sort.by(direction, entityProperty));
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
