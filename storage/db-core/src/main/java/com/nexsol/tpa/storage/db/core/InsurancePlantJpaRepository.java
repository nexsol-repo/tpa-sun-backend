package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsurancePlantJpaRepository extends JpaRepository<InsurancePlantEntity, Long> {

	Optional<InsurancePlantEntity> findByApplicationId(Long applicationId);

}