package com.akulinski.keepmeawake.core.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document
public class Question implements Serializable {

    @Id
    private String id;

    @Indexed
    private String value;

    @Indexed
    private Category category;

    private String description;
}
