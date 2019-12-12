package com.akulinski.userr8meservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rate implements Serializable, Comparable<Rate> {

  private Double rate;

  private String sender;

  private String question;

  private Instant timestamp = Instant.now();

  @Override
  public int compareTo(Rate o) {
    return this.timestamp.compareTo(o.getTimestamp());
  }
}
