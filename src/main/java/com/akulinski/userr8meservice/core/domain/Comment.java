package com.akulinski.userr8meservice.core.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Comment implements Serializable {

    private String comment;

    private String commenter;
}
