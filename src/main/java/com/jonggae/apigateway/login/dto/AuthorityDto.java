package com.jonggae.apigateway.login.dto;

import com.jonggae.apigateway.login.entity.UserRoleEnum;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDto {
    public UserRoleEnum authorityName;
    //ROLE_USER, ROLE_ADMIN 존재
}
