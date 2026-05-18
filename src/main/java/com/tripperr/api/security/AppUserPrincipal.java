package com.tripperr.api.security;

import com.tripperr.api.user.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class AppUserPrincipal implements UserDetails {

    private final String id;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    public AppUserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.enabled = user.isEnabled();
        this.authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return enabled; }
    @Override public boolean isAccountNonLocked() { return enabled; }
    @Override public boolean isCredentialsNonExpired() { return enabled; }
    @Override public boolean isEnabled() { return enabled; }
}
