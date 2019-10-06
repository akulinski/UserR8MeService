package com.akulinski.keepmeawake.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ExceptionDTO {
    private String message;
    private Instant timestamp;
}
