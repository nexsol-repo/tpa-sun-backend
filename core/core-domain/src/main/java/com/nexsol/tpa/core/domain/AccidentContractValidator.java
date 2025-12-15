package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccidentContractValidator {
    private final InsuranceApplicationReader applicationReader;

    public void validate(Long userId, Long applicationId){
        InsuranceApplication application = applicationReader.read(applicationId);

        if(!application.userId().equals(userId)){
            throw new CoreException(CoreErrorType.INSURANCE_USER_UNAUTHORIZED);
        }

        if(application.status()!= InsuranceStatus.COMPLETED){
            throw new CoreException(CoreErrorType.INSURANCE_NOT_COMPLETED);
        }
    }
}
