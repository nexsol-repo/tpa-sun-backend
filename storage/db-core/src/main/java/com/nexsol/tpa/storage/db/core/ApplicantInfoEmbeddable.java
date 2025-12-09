package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.Applicant;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class ApplicantInfoEmbeddable {

	private String companyCode;

	private String companyName;

	private String ceoName;

	private String ceoPhoneNumber;

	private String applicantName;

	private String applicantPhoneNumber;

	private String applicantEmail;

	public ApplicantInfoEmbeddable(Applicant domain) {
		this.companyCode = domain.companyCode();
		this.companyName = domain.companyName();
		this.ceoName = domain.ceoName();
		this.ceoPhoneNumber = domain.ceoPhoneNumber();
		this.applicantName = domain.applicantName();
		this.applicantPhoneNumber = domain.applicantPhoneNumber();
		this.applicantEmail = domain.applicantEmail();
	}

	public Applicant toDomain() {
		return Applicant.builder()
			.companyCode(companyCode)
			.companyName(companyName)
			.ceoName(ceoName)
			.ceoPhoneNumber(ceoPhoneNumber)
			.applicantName(applicantName)
			.applicantPhoneNumber(applicantPhoneNumber)
			.applicantEmail(applicantEmail)
			.build();
	}

}
