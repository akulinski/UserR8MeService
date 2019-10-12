package com.akulinski.keepmeawake.core.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;

@Data
@Document
public class Question implements Serializable, Persistable<String> {

    @Id
    private String id;

    @Indexed
    private String value;

    @Indexed
    private Category category;

    @Field
    private String description;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant modificationDate;

    @Override
    public boolean isNew() {
        return false;
    }
}
