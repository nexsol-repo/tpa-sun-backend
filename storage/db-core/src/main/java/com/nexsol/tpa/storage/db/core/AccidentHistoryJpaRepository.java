package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccidentHistoryJpaRepository extends JpaRepository<AccidentHistoryEntity, Long> {

	List<AccidentHistoryEntity> findByApplicationId(Long applicationId);

	void deleteByApplicationId(Long applicationId);

}
