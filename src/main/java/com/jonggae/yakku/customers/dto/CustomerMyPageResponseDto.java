package com.jonggae.yakku.customers.dto;

import com.jonggae.yakku.customers.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMyPageResponseDto {

    private String customerName;
    private String email;
    private String address;
    private String addressDetail;
    private boolean enabled;
    private Set<AuthorityDto> authorityDtoSet;

    public static CustomerMyPageResponseDto from(Customer customer) {
        return CustomerMyPageResponseDto.builder()
                .customerName(customer.getCustomerName())
                .email(customer.getEmail())
                .address(customer.getAddress().getAddress())
                .addressDetail(customer.getAddress().getAddressDetail())
                .authorityDtoSet(customer.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder()
                                .authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .enabled(customer.isEnabled())
                .build();
    }
}
