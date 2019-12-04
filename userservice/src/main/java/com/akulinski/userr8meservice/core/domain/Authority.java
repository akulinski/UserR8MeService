package com.akulinski.userr8meservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
public class Authority implements GrantedAuthority {

    private AuthorityType authorityType;

    @Override
    public String getAuthority() {
        return authorityType.name();
    }

}
