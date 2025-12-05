package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.enums.RateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface InsuranceRateJpaRepository extends JpaRepository<InsuranceRateEntity,Long> {
    @Query("""
        SELECT r FROM InsuranceRateEntity r 
        WHERE r.rateType = :rateType 
          AND r.rateKey = :rateKey 
          AND r.effectiveDate <= :date 
        ORDER BY r.effectiveDate DESC 
        LIMIT 1
    """)
    Optional<InsuranceRateEntity> findRate(
            @Param("rateType") RateType rateType,
            @Param("rateKey") String rateKey,
            @Param("date") LocalDate date);

}
