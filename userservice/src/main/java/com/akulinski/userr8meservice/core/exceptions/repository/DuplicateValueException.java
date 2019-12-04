package com.akulinski.userr8meservice.core.exceptions.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuplicateValueException extends RuntimeException {
    private String message;
}
