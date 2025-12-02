package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserReader userReader;
    private final TokenIssuer tokenIssuer;
    private final EmailVerificationReader emailVerificationReader;

    public AuthToken signIn(String CompanyCode, String email, String code) {
        User user = userReader.read(CompanyCode, email);

        EmailVerification verification = emailVerificationReader.read(email, EmailVerifiedType.SIGNIN);

        verification.checkCodeForLogin(code, LocalDateTime.now());

        return tokenIssuer.issue(user.id(), user.email());
    }
}

