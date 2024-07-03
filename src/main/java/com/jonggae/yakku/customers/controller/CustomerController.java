package com.jonggae.yakku.customers.controller;

import com.jonggae.yakku.common.apiResponse.ApiResponseDto;
import com.jonggae.yakku.common.apiResponse.ApiResponseUtil;
import com.jonggae.yakku.customers.dto.*;
import com.jonggae.yakku.customers.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    //todo: 회원가입 상황에 대응한 예외 처리 작성하기 , response 응답 형태 정리하기


    @GetMapping("/confirm")
    public ResponseEntity<ApiResponseDto<CustomerResponseDto>> confirmCustomer(@RequestParam("token") String token) {
        CustomerResponseDto customerDto = customerService.confirmCustomer(token);
        String message = "회원 가입이 완료되었습니다.";
        return ApiResponseUtil.success(message, customerDto, 200);

    }
//
}
