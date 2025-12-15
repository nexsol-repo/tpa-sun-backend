package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccidentAttachmentJpaRepository extends JpaRepository<AccidentAttachmentEntity, Long> {

	List<AccidentAttachmentEntity> findByAccidentReportId(Long accidentReportId);

}