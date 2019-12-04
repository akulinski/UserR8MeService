package com.akulinski.userr8meservice.core.exceptions.validation;

import lombok.Data;

@Data
public class InvalidUsernameException extends RuntimeException {
    private final String username;

    public InvalidUsernameException(String username){
        super(String.format("Username is too short, has to contain at least 5 characters but was: %d", username.length()));
        this.username = username;
    }
}
