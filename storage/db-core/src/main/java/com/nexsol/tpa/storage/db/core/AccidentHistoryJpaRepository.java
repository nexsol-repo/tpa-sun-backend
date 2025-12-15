package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccidentHistoryJpaRepository extends JpaRepository<AccidentHistoryEntity, Long> {

	Optional<AccidentHistoryEntity> findByApplicationId(Long applicationId);

	void deleteByApplicationId(Long applicationId);

}
