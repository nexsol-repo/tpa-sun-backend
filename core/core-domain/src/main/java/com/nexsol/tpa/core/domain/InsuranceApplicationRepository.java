package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;

import java.util.List;
import java.util.Optional;

public interface InsuranceApplicationRepository {

	InsuranceApplication save(InsuranceApplication application);

	Optional<InsuranceApplication> findById(Long id);

	List<InsuranceApplication> findAllById(List<Long> ids);

	PageResult<InsuranceApplication> findAllByUserId(Long userId, SortPage sortPage);

	List<InsuranceApplication> findAllByUserIdAndStatus(Long userId, InsuranceStatus status);

}