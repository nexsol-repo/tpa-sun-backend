package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "insurance_application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsuranceApplicationEntity extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String applicationNumber;

	// 필수 값
	@Column(nullable = false)
	private Long userId;

	// 필수 값 + Enum 타입 지정
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20)", nullable = false)
	private InsuranceStatus insuranceStatus;

	@Embedded
	private AgreementInfoEmbeddable agreementInfo;

	@Embedded
	private ApplicantInfoEmbeddable applicantInfo;

	@Embedded
	private InsurancePlantEmbeddable plantInfo;

	@Embedded
	private QuoteEmbeddable quoteInfo;

	public static InsuranceApplicationEntity fromDomain(InsuranceApplication domain) {
		InsuranceApplicationEntity entity = new InsuranceApplicationEntity();
		entity.setId(domain.id()); // ID가 있으면 설정 (Update 시)
		entity.applicationNumber = domain.applicationNumber();
		entity.userId = domain.userId();
		entity.insuranceStatus = domain.status();

		if (domain.applicant() != null) {
			entity.applicantInfo = new ApplicantInfoEmbeddable(domain.applicant());
		}
		if (domain.agreement() != null) {
			entity.agreementInfo = new AgreementInfoEmbeddable(domain.agreement());
		}
		if (domain.quote() != null) {
			entity.quoteInfo = new QuoteEmbeddable(domain.quote());
		}

		if (domain.plant() != null) {
			entity.plantInfo = new InsurancePlantEmbeddable(domain.plant());
		}
		return entity;
	}

	public void update(InsuranceApplication domain) {
		this.insuranceStatus = domain.status();
		if (domain.quote() != null) {
			this.quoteInfo = new QuoteEmbeddable(domain.quote());
		}
		if (domain.agreement() != null) {
			this.agreementInfo = new AgreementInfoEmbeddable(domain.agreement());
		}

		if (domain.plant() != null) {
			this.plantInfo = new InsurancePlantEmbeddable(domain.plant());
		}
	}

	public InsuranceApplication toDomain(InsuranceCondition condition, InsuranceDocument documents) {
		return InsuranceApplication.builder()
			.id(this.getId())
			.applicationNumber(this.applicationNumber)
			.userId(this.userId)
			.status(this.insuranceStatus)
			.applicant(this.applicantInfo != null ? this.applicantInfo.toDomain() : null)
			.agreement(this.agreementInfo != null ? this.agreementInfo.toDomain() : null)
			.quote(this.quoteInfo != null ? this.quoteInfo.toDomain() : null)
			.plant(this.plantInfo != null ? this.plantInfo.toDomain() : null)
			.condition(condition)
			.documents(documents)
			.createdAt(this.getCreatedAt())
			.updatedAt(this.getUpdatedAt())
			.build();
	}

}
