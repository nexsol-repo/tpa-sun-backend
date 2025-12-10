package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.Accident;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "accident_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccidentHistoryEntity extends BaseEntity {

	@Column(nullable = false)
	private Long applicationId;

	private LocalDate accidentDate;

	private Long accidentPayment;

	private String accidentContent;

	public static AccidentHistoryEntity fromDomain(Accident domain, Long applicationId) {
		AccidentHistoryEntity entity = new AccidentHistoryEntity();
		entity.applicationId = applicationId;
		entity.update(domain);
		return entity;
	}

	public void update(Accident domain) {
		this.accidentDate = domain.date();
		this.accidentPayment = domain.paymentAmount();
		this.accidentContent = domain.content();
	}

	public Accident toDomain() {
		return Accident.builder()
			.date(this.accidentDate)
			.paymentAmount(this.accidentPayment)
			.content(this.accidentContent)
			.build();
	}

}
