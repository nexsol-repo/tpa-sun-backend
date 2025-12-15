package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentReport;
import com.nexsol.tpa.core.domain.AccidentReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            List<AccidentAttachmentEntity> attachmentEntities = report.attachments().stream()
                    .map(att -> AccidentAttachmentEntity.from(att, reportId))
                    .toList();
            attachmentJpaRepository.saveAll(attachmentEntities);
        }


        return savedReportEntity.toDomain().toBuilder()
                .attachments(report.attachments())
                .build();
    }

    // 조회 메서드
    /*
    public Optional<AccidentReport> findById(Long id) {
        return reportJpaRepository.findById(id).map(entity -> {
            List<AccidentAttachment> attachments = attachmentJpaRepository.findByAccidentReportId(id)
                    .stream().map(AccidentAttachmentEntity::toDomain).toList();
            return entity.toDomain().toBuilder().attachments(attachments).build();
        });
    }
    */
}
