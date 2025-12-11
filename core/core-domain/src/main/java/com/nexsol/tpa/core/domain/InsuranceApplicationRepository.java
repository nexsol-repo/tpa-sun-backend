package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;

import java.util.Optional;

public interface InsuranceApplicationRepository {

	InsuranceApplication save(InsuranceApplication application);

	Optional<InsuranceApplication> findById(Long id);

	Optional<InsuranceApplication> findByApplicationNumber(String applicationNumber);

	Optional<InsuranceApplication> findWritingApplication(Long userId);

	PageResult<InsuranceApplication> findAllByUserId(Long userId, SortPage sortPage);

}