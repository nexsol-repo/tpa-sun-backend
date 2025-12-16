package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentReport;
import com.nexsol.tpa.core.domain.AccidentReportRepository;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccidentReportRepositoryImpl implements AccidentReportRepository {

	private final AccidentReportJpaRepository reportJpaRepository;

	private final AccidentAttachmentJpaRepository attachmentJpaRepository;

	@Override
	@Transactional
	public AccidentReport save(AccidentReport report) {

		AccidentReportEntity reportEntity = AccidentReportEntity.from(report);
		AccidentReportEntity savedReportEntity = reportJpaRepository.save(reportEntity);
		Long reportId = savedReportEntity.getId();
		if (report.attachments() != null && !report.attachments().isEmpty()) {
			List<AccidentAttachmentEntity> attachmentEntities = report.attachments()
				.stream()
				.map(att -> AccidentAttachmentEntity.from(att, reportId))
				.toList();
			attachmentJpaRepository.saveAll(attachmentEntities);
		}

		return savedReportEntity.toDomain().toBuilder().attachments(report.attachments()).build();
	}

	@Override
	public Optional<AccidentReport> findById(Long id) {
		return reportJpaRepository.findById(id).map(entity -> {
			List<AccidentAttachmentEntity> attachments = attachmentJpaRepository.findByAccidentReportId(id);

			return entity.toDomain()
				.toBuilder()
				.attachments(attachments.stream().map(AccidentAttachmentEntity::toDomain).toList())
				.build();
		});
	}

	@Override
	public PageResult<AccidentReport> findAllByUserId(Long userId, SortPage sortPage) {

		Pageable pageable = toJpaPageable(sortPage);

		Page<AccidentReportEntity> entityPage = reportJpaRepository.findByUserId(userId, pageable);

		List<AccidentReport> content = entityPage.getContent().stream().map(AccidentReportEntity::toDomain).toList();

		return new PageResult<>(content, entityPage.getTotalElements(), entityPage.getTotalPages(),
				entityPage.getNumber(), entityPage.hasNext());
	}

	private Pageable toJpaPageable(SortPage sortPage) {
		if (sortPage.sort() == null) {
			return PageRequest.of(sortPage.page(), sortPage.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
		}

		SortPage.Sort sort = sortPage.sort();
		Sort.Direction direction = sort.direction().isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC;

		String dbProperty = switch (sort.property()) {
			case "no" -> "id"; // No -> ID
			case "accidentNumber" -> "accidentNumber";
			case "accidentType" -> "accidentInfo.accidentType"; // Embedded 필드
			case "accidentDate" -> "accidentInfo.accidentDate";
			case "reportedAt" -> "reportedAt"; // 등록일
			case "status" -> "accidentStatus";

			case "plantName" -> "ia.plantInfo.plantName";

			default -> "createdAt";
		};

		return PageRequest.of(sortPage.page(), sortPage.size(), Sort.by(direction, dbProperty));
	}

	/**
	 * 조인이 필요한 정렬인지 판단
	 */
	private boolean isJoinRequired(SortPage sortPage) {
		if (sortPage.sort() == null)
			return false;
		return "plantName".equals(sortPage.sort().property());
	}

}
