package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.EmailVerification;
import com.nexsol.tpa.core.domain.EmailVerificationRepository;
import com.nexsol.tpa.core.enums.EmailVerifiedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {

	private final EmailVerificationJpaRepository emailVerificationJpaRepository;

	@Override
	public EmailVerification save(EmailVerification verification) {
		EmailVerificationEntity entity = emailVerificationJpaRepository
			.findByEmailAndVerifiedType(verification.email(), verification.verifiedType())
			.orElseGet(() -> EmailVerificationEntity.fromDomain(verification));

		entity.update(verification);

		return emailVerificationJpaRepository.save(entity).toDomain();
	}

	@Override
	public Optional<EmailVerification> findByCompanyCodeAndEmailAndType(String companyCode,String email, EmailVerifiedType type) {
		return emailVerificationJpaRepository.findByEmailAndVerifiedType(email, type)
			.map(EmailVerificationEntity::toDomain);
	}

}
