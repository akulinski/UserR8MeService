package com.akulinski.keepmeawake.core.domain.dto;

import com.akulinski.keepmeawake.core.domain.Category;
import lombok.Data;

@Data
public class QuestionDTO {

    private String value;

    private Category category;

    private String description;
}
