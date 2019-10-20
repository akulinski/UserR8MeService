package com.akulinski.userr8meservice.core.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Rate implements Serializable {

    private Integer rate;

    private String sender;

    private String question;
}
