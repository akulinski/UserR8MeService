package com.akulinski.userr8meservice.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateDTO {
    private String receiver;
    private Double rating;
    private String question;
}
