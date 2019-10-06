package com.akulinski.keepmeawake.core.domain;

import lombok.Data;

@Data
public class QuestionDTO {

    private String value;

    private Category category;

    private String description;
}
