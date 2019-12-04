package com.akulinski.userr8meservice.core.exceptions.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class InvalidPasswordException extends RuntimeException {

    @JsonIgnore
    private final String password;

    public InvalidPasswordException(String password){
        super("Invalid password. Did not match password criteria");
        this.password = password;
    }
}
