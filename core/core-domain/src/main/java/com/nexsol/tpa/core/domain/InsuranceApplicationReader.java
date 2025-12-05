package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
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

}
