package com.jonggae.apigateway.customer.dto;

import com.jonggae.apigateway.customer.entity.UserRoleEnum;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDto {
    public UserRoleEnum authorityName;
    //ROLE_USER, ROLE_ADMIN 존재
}
