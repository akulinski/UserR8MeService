package com.akulinski.userr8meservice.core.domain;

import com.akulinski.userr8meservice.core.domain.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
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
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Document
@Data
public class User implements UserDetails, Serializable, Persistable<String> {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @JsonIgnore
    @Field
    private String password;

    @Email
/*
    @Indexed(unique = true)
*/
    private String email;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant modificationDate;

    @Indexed
    private String link;

    @Field
    private Boolean isEnabled = Boolean.TRUE;

    @Field
    private Set<Rate> rates = new HashSet<>();

    @Field
    private Map<String, Double> ratesMap = new HashMap();

    //-1 means no rates assigned
    @Field
    private Double currentRating = -1.0;

    @Field
    private Set<Comment> comments = new HashSet<>();

    @Field
    private Set<Authority> authorities = new HashSet<>();

    @Field
    @JsonIgnore
    private Set<UserDTO> following = new HashSet<>();

    @Field
    @JsonIgnore
    private Set<UserDTO> followers = new HashSet<>();

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
