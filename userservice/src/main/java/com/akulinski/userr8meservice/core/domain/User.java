package com.akulinski.userr8meservice.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Document
@Data
@AllArgsConstructor
public class User implements UserDetails, Serializable, Persistable<String> {

  @Id
  @JsonIgnore
  private String id;

  @Indexed(unique = true)
  @NotBlank
  @NotNull
  private String username;

  @JsonIgnore
  @Field
  @NotBlank
  @NotNull
  @Length(min = 7)
  private String password;

  @NotBlank
  @NotNull
  @Email
  @Indexed(unique = true)
  @JsonIgnore
  private String email;

  @CreatedDate
  private Instant created;

  @LastModifiedDate
  @JsonIgnore
  private Instant modificationDate;

  @Indexed
  private String link;

  @Field
  @JsonIgnore
  private Boolean isEnabled = Boolean.TRUE;

  @Field
  @JsonIgnore
  private SortedSet<Rate> rates;

  @Field
  @JsonIgnore
  private Map<String, Double> ratesMap;

  //-1 means no rates assigned
  @Field
  private Double currentRating = -1.0;

  @Field
  @JsonIgnore
  private SortedSet<Comment> comments;

  @Field
  @NotEmpty
  @NotNull
  @JsonIgnore
  private Set<Authority> authorities;

  @Field
  @JsonIgnore
  private Set<String> following;

  @Field
  @JsonIgnore
  private Set<String> followers;

  public User() {
    this.rates = new TreeSet<>();
    this.ratesMap = new HashMap<>();
    this.comments = new TreeSet<>();
    this.authorities = new HashSet<>();
    this.following = new HashSet<>();
    this.followers = new HashSet<>();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

  @Override
  public boolean isNew() {
    return !isEnabled;
  }
}
