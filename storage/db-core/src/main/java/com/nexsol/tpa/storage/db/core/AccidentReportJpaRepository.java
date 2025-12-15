package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccidentReportJpaRepository extends JpaRepository<AccidentReportEntity, Long> {

	Page<AccidentReportEntity> findByUserId(Long userId,Pageable pageable);

	@Query(value = """
        SELECT ar
        FROM AccidentReportEntity ar
        LEFT JOIN InsuranceApplicationEntity ia ON ar.applicationId = ia.id
        WHERE ar.userId = :userId
    """,
			countQuery = """
        SELECT count(ar)
        FROM AccidentReportEntity ar
        WHERE ar.userId = :userId
    """)
	Page<AccidentReportEntity> findByUserIdWithJoin(@Param("userId") Long userId, Pageable pageable);
}
