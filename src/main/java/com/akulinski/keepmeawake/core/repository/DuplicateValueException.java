package com.akulinski.keepmeawake.core.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuplicateValueException extends RuntimeException {
    private String message;
}
