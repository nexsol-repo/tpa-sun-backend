package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceApplicationService {
    private final UserReader userReader;
    private final InsuranceApplicationReader applicationReader;
    private final InsuranceApplicationWriter applicationWriter;
    private final InsuranceInspector insuranceInspector;
    private final InsurancePremiumCalculator premiumCalculator;


    public InsuranceApplication getInsuranceApplication(Long applicationId) {
        return applicationReader.read(applicationId);
    }


    public InsuranceApplication savePlantInit(Long userId, AgreementInfo agreementInfo) {
        User user = userReader.read(userId);

        ApplicantInfo applicantInfo = ApplicantInfo.builder()
                .companyCode(user.companyCode())
                .companyName(user.companyName())
                .ceoName(user.name())
                .ceoPhoneNumber(user.phoneNumber())
                .applicantName(user.applicantName())
                .applicantPhoneNumber(user.applicantPhoneNumber())
                .email(user.email())
                .build();

        // TODO SUN: 추후 바뀜 현재는 하드코딩해야함
        String applicationNumber = "2025-NO-TEST-1234";

        InsuranceApplication newInsurance = InsuranceApplication.create(userId, applicationNumber, applicantInfo, agreementInfo);

        return applicationWriter.writer(newInsurance);

    }


    public InsuranceApplication savePlantInfo(Long applicationId, InsurancePlant plantInfo) {
        InsuranceApplication application = applicationReader.read(applicationId);

        InsuranceApplication updated = application.updatePlantInfo(plantInfo);

        return applicationWriter.writer(updated);
    }

    public InsuranceApplication saveCondition(Long applicationId, InsuranceCondition condition) {
        InsuranceApplication application = applicationReader.read(applicationId);

        insuranceInspector.inspectCondition(condition);

        InsuranceApplication updated = application.updateCondition(condition);

        if (updated.plantInfo() != null) {
            InsuranceCoverage coverage = premiumCalculator.calculate(updated.plantInfo(), condition);

            updated = updated.updateCoverage(coverage);
        }

        return applicationWriter.writer(updated);
    }

    public InsuranceApplication completeApplication(Long applicationId) {
        InsuranceApplication app = applicationReader.read(applicationId);


        validateForCompletion(app);


        return applicationWriter.writer(app);
    }
    private void validateForCompletion(InsuranceApplication app) {
        if (app.plantInfo() == null) throw new CoreException(CoreErrorType.INVALID_INPUT, "발전소 정보가 입력되지 않았습니다.");
        if (app.condition() == null) throw new CoreException(CoreErrorType.INVALID_INPUT, "가입 조건이 입력되지 않았습니다.");
        if (app.coverage() == null) throw new CoreException(CoreErrorType.INVALID_INPUT, "보험료 산출이 완료되지 않았습니다.");
    }

}
