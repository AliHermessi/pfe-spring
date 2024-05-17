package com.project.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.project.models.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OurUserInfoDetails implements UserDetails {
    private String cin;
    private String password;
    private List<GrantedAuthority> roles;

    public OurUserInfoDetails(User user) {
        this.cin = user.getCin();
        this.password = user.getPassword();
        this.roles = user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.cin;
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
