package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsuranceApplicationValidator {

	private final InsuranceApplicationRepository insuranceApplicationRepository;

	public void checkDuplicatePlantName(String companyCode, String plantName, Long currentApplicationId) {
		if (insuranceApplicationRepository.existsByCompanyCodeAndPlantName(companyCode, plantName,
				currentApplicationId)) {

			throw new CoreException(CoreErrorType.INSURANCE_DUPLICATE_PLANT_DATA);
		}
	}

	public boolean exists(String companyCode, String plantName, Long excludeId) {
		return insuranceApplicationRepository.existsByCompanyCodeAndPlantName(companyCode, plantName, excludeId);
	}

}
