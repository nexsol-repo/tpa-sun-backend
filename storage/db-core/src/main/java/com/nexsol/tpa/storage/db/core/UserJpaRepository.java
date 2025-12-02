package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByCompanyCodeAndEmail(String companyCode, String email);
    boolean existsByCompanyCodeAndEmail(String companyCode, String email);
}
