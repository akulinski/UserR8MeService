package com.akulinski.keepmeawake.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document
@Data
public class User {

    @Id
    private String id;

    @Indexed
    private String username;

    @JsonIgnore
    private String password;

    @Email
    private String email;

    private Instant created;

    private Set<Category> categories = new HashSet<>();

    @DBRef(lazy = true)
    private Set<Question> askedQuestions = new HashSet<>();

}
