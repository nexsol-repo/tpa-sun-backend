package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.enums.InsuranceStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsuranceApplicationJpaRepository extends JpaRepository<InsuranceApplicationEntity, Long> {

	Optional<InsuranceApplicationEntity> findByApplicationNumber(String applicationNumber);

	Optional<InsuranceApplicationEntity> findByUserIdAndStatus(Long userId, InsuranceStatus status);

	List<InsuranceApplicationEntity> findAllByUserIdAndInsuranceStatus(Long userId, InsuranceStatus insuranceStatus);

	Page<InsuranceApplicationEntity> findByUserId(Long userId, Pageable pageable);

	boolean existsByApplicantInfo_CompanyCodeAndPlantInfo_PlantNameAndIdNot(String companyCode, String plantName,
			Long id);

}
