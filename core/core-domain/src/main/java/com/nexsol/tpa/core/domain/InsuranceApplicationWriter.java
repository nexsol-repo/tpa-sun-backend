package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsuranceApplicationWriter {

	private final InsuranceApplicationRepository insuranceApplicationRepository;

	InsuranceApplication writer(InsuranceApplication insuranceApplication) {
		return insuranceApplicationRepository.save(insuranceApplication);
	}

}
