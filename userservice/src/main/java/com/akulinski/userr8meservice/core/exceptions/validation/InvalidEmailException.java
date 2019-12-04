package com.akulinski.userr8meservice.core.exceptions.validation;

import lombok.Data;

@Data
public class InvalidEmailException extends RuntimeException {

    private final String email;

    public InvalidEmailException(String email) {
        super(String.format("Invalid email format: %s", email));
        this.email = email;
    }
}
