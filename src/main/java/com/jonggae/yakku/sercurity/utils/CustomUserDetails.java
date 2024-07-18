package com.jonggae.yakku.sercurity.utils;

import com.jonggae.yakku.customers.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Customer customer;

    private final Collection<? extends GrantedAuthority> authorities;

    public Long getCustomerId() {
        return customer.getId();
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
    public boolean isEnabled() {
        return customer.isEnabled();  // customer의 상태를 반영
    }

}

