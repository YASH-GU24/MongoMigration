package io.zoooohs.realworld.security;

import lombok.Builder;
import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AuthUserDetails implements UserDetails {
    private final ObjectId objectId;
    private final Long id;
    private final String email;

    @Builder
    public AuthUserDetails(ObjectId objectId, Long id, String email) {
        this.objectId = objectId;
        this.id = id;
        this.email = email;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // no authority in this project
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
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
