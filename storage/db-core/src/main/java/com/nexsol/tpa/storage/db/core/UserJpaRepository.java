package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByCompanyCodeAndApplicantEmail(String companyCode, String email);

	boolean existsByCompanyCodeAndApplicantEmail(String companyCode, String email);

}
