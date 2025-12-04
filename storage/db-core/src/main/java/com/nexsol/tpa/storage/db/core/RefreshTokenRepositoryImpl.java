package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.RefreshToken;
import com.nexsol.tpa.core.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

	private final RefreshTokenJpaRepository refreshTokenJpaRepository;

	@Override
	public void save(RefreshToken refreshToken) {
		refreshTokenJpaRepository.save(RefreshTokenEntity.fromDomain(refreshToken));
	}

	@Override
	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenJpaRepository.findByToken(token).map(RefreshTokenEntity::toDomain);
	}

	@Override
	public void deleteByToken(String token) {
		refreshTokenJpaRepository.deleteByToken(token);
	}

}
