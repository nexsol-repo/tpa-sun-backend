package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccidentReportJpaRepository extends JpaRepository<AccidentReportEntity, Long> {

	List<AccidentReportEntity> findByUserId(Long userId);

}
