package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.enums.InsuranceStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceApplicationJpaRepository extends JpaRepository<InsuranceApplicationEntity, Long> {

	Optional<InsuranceApplicationEntity> findByApplicationNumber(String applicationNumber);

	Optional<InsuranceApplicationEntity> findByUserIdAndStatus(Long userId, InsuranceStatus status);

	Page<InsuranceApplicationEntity> findByUserId(Long userId, Pageable pageable);

}
