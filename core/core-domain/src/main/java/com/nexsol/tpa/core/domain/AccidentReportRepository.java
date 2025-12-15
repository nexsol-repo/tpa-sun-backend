package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;

import java.util.Optional;

public interface AccidentReportRepository {

	AccidentReport save(AccidentReport report);
	Optional<AccidentReport> findById(Long id);
	PageResult<AccidentReport> findAllByUserId(Long userId, SortPage sortPage);

}
