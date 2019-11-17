package com.akulinski.userr8meservice.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateDTO {
    private String receiver;
    private Double rating;
    private String question;
}
