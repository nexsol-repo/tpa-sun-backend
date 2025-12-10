package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

	private String companyCode;

	private String companyName;

	private String name;

	private String phoneNumber;

	private String applicantName;

	private String applicantEmail;

	private String applicantPhoneNumber;

	public static UserEntity fromDomain(User user) {
		UserEntity entity = new UserEntity();
		entity.setId(user.id());
		entity.companyCode = user.companyCode();
		entity.update(user);

		return entity;
	}

	public void update(User user) {

		this.companyName = user.companyName();
		this.name = user.name();
		this.phoneNumber = user.phoneNumber();
		this.applicantName = user.applicantName();
		this.applicantEmail = user.applicantEmail();
		this.applicantPhoneNumber = user.applicantPhoneNumber();
	}

	public User toDomain() {
		return User.builder()
			.id(this.getId())
			.companyCode(this.companyCode)
			.companyName(this.companyName)
			.name(this.name)
			.phoneNumber(this.phoneNumber)
			.applicantName(this.applicantName)
			.applicantEmail(this.applicantEmail)
			.applicantPhoneNumber(this.applicantPhoneNumber)
			.build();
	}

}
