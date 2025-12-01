package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserReader userReader;
    private final UserAppender userAppender;
    private final EmailVerificationReader emailVerificationReader;

    public User signUp(NewUser newUser) {
        emailVerificationReader.ensureVerified(newUser.email());

        boolean exists = userReader.exists(newUser.companyCode(), newUser.email());
        if (exists) {
            throw new CoreException(CoreErrorType.USER_EXIST_DATA);
        }
        return userAppender.append(newUser.toUser());
    }

    public User update(User user, ModifyUser modifyUser) {
        User updatedUser = user.update(modifyUser);

        return userAppender.append(updatedUser);
    }
}
