package com.jonggae.apigateway.sercurity.utils;

import com.jonggae.apigateway.login.dto.CustomerResponseDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final CustomerResponseDto customer;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(CustomerResponseDto customer, Collection<? extends GrantedAuthority> authorities) {
        this.customer = customer;
        this.authorities = authorities;
    }

    public Long getCustomerId() {
        return customer.getCustomerId();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getCustomerName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return customer.isEnabled();  // customer의 상태를 반영
    }

}

