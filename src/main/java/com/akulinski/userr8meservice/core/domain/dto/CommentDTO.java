package com.akulinski.userr8meservice.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO implements Serializable {
    private String comment;
    private String commenter;
    private String receiver;
}
