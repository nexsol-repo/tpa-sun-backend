package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuranceAttachmentJpaRepository extends JpaRepository<InsuranceAttachmentEntity, Long> {

	List<InsuranceAttachmentEntity> findByApplicationId(Long applicationId);

	void deleteByApplicationId(Long applicationId);

}
