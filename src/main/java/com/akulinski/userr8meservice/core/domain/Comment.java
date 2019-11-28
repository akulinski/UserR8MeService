package com.akulinski.userr8meservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

  private String comment;

  private String commenterLogin;

  private String commenterLink;

  private Instant timestamp;
}
