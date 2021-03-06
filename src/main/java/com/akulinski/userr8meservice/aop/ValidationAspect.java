package com.akulinski.userr8meservice.aop;

import com.akulinski.userr8meservice.core.domain.User;
import com.akulinski.userr8meservice.core.domain.dto.ChangePasswordDTO;
import com.akulinski.userr8meservice.core.domain.dto.CreateUserDTO;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidEmailException;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidPasswordException;
import com.akulinski.userr8meservice.core.exceptions.validation.InvalidUsernameException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Aspect
@Slf4j
@Component
public class ValidationAspect {

    private final Pattern digitPattern;
    private final Pattern specialPattern;

    private static final String EMAIL_REGEX = "^(.+)@(.+)$";

    private final Pattern emailPattern;

    private final int usernameLen = 5;

    private final int passwordLen = 7;

    public ValidationAspect() {
        this.emailPattern = Pattern.compile(EMAIL_REGEX);
        digitPattern = Pattern.compile("[0-9]");

        specialPattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
    }


    @Before("execution(* com.akulinski.userr8meservice.core.repository.UserRepository.save(..)) && args(user)")
    public void validate(JoinPoint joinPoint, User user) {

        final var email = user.getEmail();

        final var username = user.getUsername();

        if (!emailPattern.matcher(email).matches()) {
            log.warn(String.format("Validation of email failed for: %s %s", email, joinPoint.getSignature()));
            throw new InvalidEmailException(email);
        }

        if (basicStringCheck(username, usernameLen)) {
            log.warn(String.format("Validation of username failed for: %s %s", username, joinPoint.getSignature()));
            throw new InvalidUsernameException(username);
        }
    }

    @Before("execution(* com.akulinski.userr8meservice.core.services.UserService.getUser(..)) && args(createUserDTO)")
    public void validatePassword(CreateUserDTO createUserDTO) {

        final var password = createUserDTO.getPassword();

        if (basicStringCheck(password, passwordLen) || passwordCheck(password)) {
            log.warn("Validation of password failed");
            throw new InvalidPasswordException(password);
        }
    }

    @Before("execution(* com.akulinski.userr8meservice.core.services.UserService.changePassword(..)) && args(username,changePasswordDTO)")
    public void validatePasswordChange(String username, ChangePasswordDTO changePasswordDTO) {
        final var password = changePasswordDTO.getNewPassword();

        if (basicStringCheck(password, passwordLen) || passwordCheck(password)) {
            log.warn("Validation of new password failed");
            throw new InvalidPasswordException(password);
        }
    }

    //Big and small letters numbers and special chars
    private boolean passwordCheck(String password) {
        var digits = digitPattern.matcher(password).find();
        var specialChars = specialPattern.matcher(password).find();
        var mixedCase = StringUtils.isMixedCase(password);

        return !digits || !specialChars || !mixedCase;
    }

    private boolean basicStringCheck(String str, int len) {
        return str == null || StringUtils.isEmpty(str) || str.length() < len;
    }
}
