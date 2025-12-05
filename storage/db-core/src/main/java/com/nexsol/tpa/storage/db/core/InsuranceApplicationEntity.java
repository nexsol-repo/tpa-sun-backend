package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsuranceApplication;
import com.nexsol.tpa.core.enums.InsuranceStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsuranceApplicationEntity extends BaseEntity{
    @Column(nullable = false, unique = true)
    private String applicationNumber;

    // 필수 값
    @Column(nullable = false)
    private Long userId;

    // 필수 값 + Enum 타입 지정
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private InsuranceStatus status;

    @Embedded
    private AgreementInfoEmbeddable agreementInfo;

    @Embedded
    private ApplicantInfoEmbeddable applicantInfo;

    @Embedded
    private PlantInfoEmbeddable plantInfo;

    @Embedded
    private ConditionEmbeddable conditionInfo;

    @Embedded
    private CoverageEmbeddable coverageInfo;

    private LocalDateTime signedAt;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;

    public static InsuranceApplicationEntity fromDomain(InsuranceApplication domain) {
        InsuranceApplicationEntity entity = new InsuranceApplicationEntity();
        entity.setId(domain.id());
        entity.applicationNumber = domain.applicationNumber();
        entity.userId = domain.userId();
        entity.status = domain.status();

        if (domain.agreementInfo() != null) entity.agreementInfo = new AgreementInfoEmbeddable(domain.agreementInfo());
        if (domain.applicantInfo() != null) entity.applicantInfo = new ApplicantInfoEmbeddable(domain.applicantInfo());

        entity.update(domain); // 상세 정보 매핑

        return entity;
    }

    public void update(InsuranceApplication domain) {
        this.status = domain.status();

        if (domain.plantInfo() != null) this.plantInfo = new PlantInfoEmbeddable(domain.plantInfo());
        if (domain.condition() != null) this.conditionInfo = new ConditionEmbeddable(domain.condition());
        if (domain.coverage() != null) this.coverageInfo = new CoverageEmbeddable(domain.coverage());
    }

    public InsuranceApplication toDomain() {
        return InsuranceApplication.builder()
                .id(this.getId())
                .applicationNumber(this.applicationNumber)
                .userId(this.userId)
                .status(this.status)
                .agreementInfo(this.agreementInfo != null ? this.agreementInfo.toDomain() : null)
                .applicantInfo(this.applicantInfo != null ? this.applicantInfo.toDomain() : null)
                .plantInfo(this.plantInfo != null ? this.plantInfo.toDomain() : null)
                .condition(this.conditionInfo != null ? this.conditionInfo.toDomain() : null)
                .coverage(this.coverageInfo != null ? this.coverageInfo.toDomain() : null)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
}
