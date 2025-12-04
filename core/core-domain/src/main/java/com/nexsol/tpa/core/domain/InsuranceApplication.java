package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsuranceApplication(
        Long id,
        String applicationNumber,
        Long userId,
        ApplicantInfo applicantInfo,
        InsurancePlant plantInfo,
        InsuranceCondition condition,
        InsuranceCoverage coverage,
        AgreementInfo agreementInfo,
        InsuranceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt


) {

    public static InsuranceApplication create(Long userId, String applicationNumber, ApplicantInfo applicantInfo, AgreementInfo agreementInfo){
        return InsuranceApplication.builder()
                .userId(userId)
                .applicationNumber(applicationNumber)
                .applicantInfo(applicantInfo)
                .agreementInfo(agreementInfo)
                .status(InsuranceStatus.PENDING)
                .build();
    }


    public InsuranceApplication updatePlantInfo(InsurancePlant newPlantInfo) {
        return InsuranceApplication.builder()
                .id(this.id)
                .applicationNumber(this.applicationNumber)
                .userId(this.userId)
                .applicantInfo(this.applicantInfo)
                .plantInfo(newPlantInfo) // 업데이트
                .condition(this.condition)
                .coverage(this.coverage)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }

    // 3단계: 가입 조건 입력
    public InsuranceApplication updateCondition(InsuranceCondition newCondition) {
        return InsuranceApplication.builder()
                .id(this.id)
                .applicationNumber(this.applicationNumber)
                .userId(this.userId)
                .applicantInfo(this.applicantInfo)
                .plantInfo(this.plantInfo)
                .condition(newCondition) // 업데이트
                .coverage(this.coverage)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }

    // 4단계: 보험료 산출 결과 적용
    public InsuranceApplication updateCoverage(InsuranceCoverage newCoverage) {
        return InsuranceApplication.builder()
                .id(this.id)
                // ... (나머지 필드 복사)
                .plantInfo(this.plantInfo)
                .condition(this.condition)
                .coverage(newCoverage) // 업데이트
                .status(this.status)
                // ...
                .build();
    }
}
