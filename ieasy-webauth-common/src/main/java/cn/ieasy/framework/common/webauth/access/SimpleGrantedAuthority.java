package cn.ieasy.framework.common.webauth.access;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class SimpleGrantedAuthority
        implements GrantedAuthority {

    /**
     *
     */
    private static final long serialVersionUID = 1157674755348505646L;
    private String role;

    public SimpleGrantedAuthority() {
    }

    public SimpleGrantedAuthority(final String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return this.role;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SimpleGrantedAuthority) {
            return this.role.equals(((SimpleGrantedAuthority) obj).role);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(final String role) {
        this.role = role;
    }
}
