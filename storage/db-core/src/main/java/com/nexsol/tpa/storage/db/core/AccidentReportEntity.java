package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentReport;
import com.nexsol.tpa.core.enums.AccidentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "accident_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccidentReportEntity extends BaseEntity {

	private Long userId;

	@Column(nullable = false, unique = true)
	private String accidentNumber;

	private Long applicationId;

	@Enumerated(EnumType.STRING)
	private AccidentStatus accidentStatus;

	private LocalDateTime reportedAt;

	private String plantName;

	private String insuredName;

	private String insuredPhone;

	@Embedded
	private AccidentInfoEmbeddable accidentInfo;

	public static AccidentReportEntity from(AccidentReport domain) {
		AccidentReportEntity entity = new AccidentReportEntity();
		entity.userId = domain.userId();
		entity.accidentNumber = domain.accidentNumber();
		entity.applicationId = domain.applicationId();
		entity.accidentStatus = domain.status();
		entity.reportedAt = domain.reportedAt();
		entity.accidentInfo = new AccidentInfoEmbeddable(domain.accidentInfo());
		entity.plantName = domain.plantName();
		entity.insuredName = domain.insuredName();
		entity.insuredPhone = domain.insuredPhone();
		return entity;
	}

	public AccidentReport toDomain() {
		return AccidentReport.builder()
			.id(this.getId())
			.userId(this.userId)
			.accidentNumber(this.accidentNumber)
			.applicationId(this.applicationId)
			.status(this.accidentStatus)
			.reportedAt(this.reportedAt)
			.accidentInfo(this.accidentInfo != null ? this.accidentInfo.toDomain() : null)
			.plantName(this.plantName)
			.insuredName(this.insuredName)
			.insuredPhone(this.insuredPhone)
			.build();
	}

}
