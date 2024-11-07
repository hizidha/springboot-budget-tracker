package com.devland.assignment.finalproject.authentication.model;

import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserPrincipal implements UserDetails {
    @Getter
    private final Long id;

    @Getter
    private final String name;

    private final String username;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(Long id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.authorities = Collections.emptyList();
    }

    public static UserDetails build(ApplicationUser applicationUser) {
        return new UserPrincipal(applicationUser.getId(), applicationUser.getName(),
                                 applicationUser.getUsername(), applicationUser.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}