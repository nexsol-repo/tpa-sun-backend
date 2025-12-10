package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceConditionJpaRepository extends JpaRepository<InsuranceConditionEntity, Long> {

	Optional<InsuranceConditionEntity> findByApplicationId(Long applicationId);

}
