package com.akulinski.userr8meservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rate implements Serializable {

    private Double rate;

    private String sender;

    private String question;
}
