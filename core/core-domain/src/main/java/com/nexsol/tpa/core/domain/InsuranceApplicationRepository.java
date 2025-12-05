package com.nexsol.tpa.core.domain;

import java.util.Optional;

public interface InsuranceApplicationRepository {

	InsuranceApplication save(InsuranceApplication application);

	Optional<InsuranceApplication> findById(Long id);

	Optional<InsuranceApplication> findByApplicationNumber(String applicationNumber);

	Optional<InsuranceApplication> findWritingApplication(Long userId);

}