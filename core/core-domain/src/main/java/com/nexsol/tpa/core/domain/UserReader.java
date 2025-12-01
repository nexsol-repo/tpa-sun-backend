package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;

    public User read(String companyCode,String email){
        return userRepository.findByCompanyCodeAndEmail(companyCode,email).orElseThrow(()-> new CoreException(CoreErrorType.USER_NOT_FOUND));
    }

    public boolean exists(String companyCode,String email){
        return userRepository.existsCompanyCodeAndEmail(companyCode,email);
    }
}
