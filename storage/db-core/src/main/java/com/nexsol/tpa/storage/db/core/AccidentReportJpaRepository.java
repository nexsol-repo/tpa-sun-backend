package com.nexsol.tpa.storage.db.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccidentReportJpaRepository extends JpaRepository<AccidentReportEntity, Long> {

	Page<AccidentReportEntity> findByUserId(Long userId, Pageable pageable);

}
