package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsuranceApplicationReader {

	private final InsuranceApplicationRepository insuranceApplicationRepository;

	public InsuranceApplication read(Long applicationId) {
		return insuranceApplicationRepository.findById(applicationId)
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));
	}

	public PageResult<InsuranceApplication> readAll(Long userId, SortPage sortPage) {
		return insuranceApplicationRepository.findAllByUserId(userId, sortPage);
	}

}
