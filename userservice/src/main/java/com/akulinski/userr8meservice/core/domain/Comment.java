package com.akulinski.userr8meservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable, Comparable<Comment> {

  private String comment;

  private String commenterLogin;

  private String commenterLink;

  private Instant timestamp = Instant.now();

  @Override
  public int compareTo(Comment o) {
    return this.timestamp.compareTo(o.getTimestamp());
  }
}
