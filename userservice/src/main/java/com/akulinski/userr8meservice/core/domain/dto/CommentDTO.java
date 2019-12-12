package com.akulinski.userr8meservice.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO implements Serializable {

  @NotNull
  @NotEmpty
  private String comment;

  @NotNull
  @NotEmpty
  private String receiver;
}
