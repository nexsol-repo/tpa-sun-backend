package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReader userReader;

    private final UserFinder userFinder;

    private final UserAppender userAppender;

    private final EmailUpdateValidator emailUpdateValidator;

    private final EmailVerificationReader emailVerificationReader;


    public User findUser(Long userId) {
        return userFinder.find(userId);
    }

    public User signUp(NewUser newUser) {
        EmailVerification verification = emailVerificationReader.read(newUser.companyCode(),newUser.applicantEmail(),
                EmailVerifiedType.SIGNUP);

        verification.validateSignup(LocalDateTime.now());


        return userAppender.append(newUser.toUser());
    }

    public User update(Long userId, ModifyUser modifyUser) {
        User user = userReader.read(userId);

        emailUpdateValidator.validate(user, modifyUser);

        User updatedUser = user.update(modifyUser);


        return userAppender.append(updatedUser);
    }

}
