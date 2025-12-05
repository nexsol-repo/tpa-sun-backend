package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsuranceApplication;
import com.nexsol.tpa.core.domain.InsuranceApplicationRepository;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InsuranceApplicationRepositoryImpl implements InsuranceApplicationRepository {

	private final InsuranceApplicationJpaRepository insuranceApplicationJpaRepository;

	@Override
	public InsuranceApplication save(InsuranceApplication application) {
		InsuranceApplicationEntity entity;
		if (application.id() == null) {
			entity = InsuranceApplicationEntity.fromDomain(application);

		}
		else {
			entity = insuranceApplicationJpaRepository.findById(application.id())
				.orElseGet(() -> InsuranceApplicationEntity.fromDomain(application));
			entity.update(application);
		}
		return insuranceApplicationJpaRepository.save(entity).toDomain();

	}

	@Override
	public Optional<InsuranceApplication> findById(Long id) {
		return insuranceApplicationJpaRepository.findById(id).map(InsuranceApplicationEntity::toDomain);
	}

	@Override
	public Optional<InsuranceApplication> findByApplicationNumber(String applicationNumber) {
		return insuranceApplicationJpaRepository.findByApplicationNumber(applicationNumber)
			.map(InsuranceApplicationEntity::toDomain);
	}

	@Override
	public Optional<InsuranceApplication> findWritingApplication(Long userId) {
		return insuranceApplicationJpaRepository.findByUserIdAndStatus(userId, InsuranceStatus.PENDING)
			.map(InsuranceApplicationEntity::toDomain);
	}

}
