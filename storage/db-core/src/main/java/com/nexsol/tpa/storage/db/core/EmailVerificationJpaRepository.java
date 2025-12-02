package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerificationEntity, Long> {

	Optional<EmailVerificationEntity> findByEmailAndVerifiedType(String email, EmailVerifiedType type);

}
