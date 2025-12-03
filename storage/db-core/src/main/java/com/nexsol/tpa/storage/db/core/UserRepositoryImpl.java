package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.User;
import com.nexsol.tpa.core.domain.UserRepository;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public Optional<User> findByCompanyCodeAndEmail(String companyCode, String email) {
		return userJpaRepository.findByCompanyCodeAndEmail(companyCode, email).map(UserEntity::toDomain);
	}

	@Override
	public Optional<User> findById(Long id) {
		return userJpaRepository.findById(id).map(UserEntity::toDomain);
	}

	@Override
	public boolean existsCompanyCodeAndEmail(String companyCode, String email) {
		return userJpaRepository.existsByCompanyCodeAndEmail(companyCode, email);
	}

	@Override
	public User save(User user) {
		if (user.id() == null) {

			return userJpaRepository.save(UserEntity.fromDomain(user)).toDomain();
		}
		else {

			UserEntity entity = userJpaRepository.findById(user.id())
				.orElseThrow(() -> new CoreException(CoreErrorType.USER_NOT_FOUND));

			entity.update(user);

			return userJpaRepository.save(entity).toDomain();
		}
	}

}
