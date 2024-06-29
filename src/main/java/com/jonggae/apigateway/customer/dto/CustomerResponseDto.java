package com.jonggae.apigateway.customer.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CustomerResponseDto {

    private String customerName;

    private String password;

    private String email;

    private String address;

    private String addressDetail;

    private boolean enabled;

    private Set<AuthorityDto> authorityDtoSet;

}
