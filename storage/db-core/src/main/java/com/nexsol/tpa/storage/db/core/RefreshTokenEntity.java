package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.RefreshToken;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity extends BaseEntity {

	private String token;

	private Long userId;

	private LocalDateTime issuedAt;

	private LocalDateTime expiredAt;

	public static RefreshTokenEntity fromDomain(RefreshToken domain) {
		RefreshTokenEntity entity = new RefreshTokenEntity();
		entity.token = domain.token();
		entity.userId = domain.userId();
		entity.expiredAt = domain.expiredAt();
		return entity;
	}

	public RefreshToken toDomain() {
		return RefreshToken.builder().token(this.token).userId(this.userId).expiredAt(this.expiredAt).build();
	}

	public void updateToken(String newToken, LocalDateTime newExpiredAt) {
		this.token = newToken;
		this.expiredAt = newExpiredAt;
	}

}
