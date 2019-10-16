package com.akulinski.keepmeawake.core.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentDTO implements Serializable {
    private String comment;
    private String commenter;
    private String receiver;
}
