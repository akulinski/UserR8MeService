package com.akulinski.keepmeawake.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Document
@Data
public class User implements UserDetails {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
        return true;
    }
}
