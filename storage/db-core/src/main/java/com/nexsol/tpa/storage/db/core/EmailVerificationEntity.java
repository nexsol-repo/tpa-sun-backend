package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.EmailVerification;
import com.nexsol.tpa.core.enums.EmailVerifiedType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationEntity extends BaseEntity {

	private String email;

	private String code;

	private boolean isVerified;

	private int attemptCount;

	@Enumerated(EnumType.STRING)
	private EmailVerifiedType verifiedType;

	private LocalDateTime sentAt;

	private LocalDateTime expiredAt;

	private LocalDateTime verifiedAt;

	public static EmailVerificationEntity fromDomain(EmailVerification verification) {
		EmailVerificationEntity entity = new EmailVerificationEntity();
		entity.email = verification.email();
		entity.code = verification.code();
		entity.isVerified = verification.isVerified();
		entity.attemptCount = verification.attemptCount();
		entity.verifiedType = verification.verifiedType();
		entity.sentAt = verification.sentAt();
		entity.expiredAt = verification.expiredAt();
		entity.verifiedAt = verification.verifiedAt();
		return entity;
	}

	public void update(EmailVerification domain) {
		this.code = domain.code();
		this.isVerified = domain.isVerified();
		this.attemptCount = domain.attemptCount();
		this.sentAt = domain.sentAt();
		this.expiredAt = domain.expiredAt();
		this.verifiedAt = domain.verifiedAt();
	}

	public EmailVerification toDomain() {
		return EmailVerification.builder()
			.email(this.email)
			.code(this.code)
			.isVerified(this.isVerified)
			.attemptCount(this.attemptCount)
			.verifiedType(this.verifiedType)
			.sentAt(this.sentAt)
			.expiredAt(this.expiredAt)
			.verifiedAt(this.verifiedAt)
			.build();
	}

}
