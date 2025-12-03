package com.nexsol.tpa.core.domain;

import java.util.Optional;

public interface RefreshTokenRepository {

	void save(RefreshToken refreshToken);

	Optional<RefreshToken> findByToken(String token);

	void deleteByToken(String token); // (선택) 로그아웃이나 로테이션 시 사용

}