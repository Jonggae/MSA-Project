package com.jonggae.yakku.customers.controller;


import com.jonggae.yakku.common.apiResponse.ApiResponseDto;
import com.jonggae.yakku.common.apiResponse.ApiResponseUtil;
import com.jonggae.yakku.customers.dto.CustomerMyPageResponseDto;
import com.jonggae.yakku.customers.dto.CustomerRequestDto;
import com.jonggae.yakku.customers.dto.CustomerResponseDto;
import com.jonggae.yakku.customers.dto.CustomerUpdateDto;
import com.jonggae.yakku.customers.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerApiController {

    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerApiController.class);

    @GetMapping("/my-page")
    public ResponseEntity<ApiResponseDto<CustomerMyPageResponseDto>> myPage(@RequestHeader("customerName") String customerName) {
        logger.debug("myPage endpoint called with customerName: " + customerName);

        CustomerMyPageResponseDto getCustomerDto = customerService.getMyPage(customerName);
        String message = "회원 정보";
        return ApiResponseUtil.success(message, getCustomerDto, 200);
    }

    @PutMapping("/my-page/update")
    public ResponseEntity<ApiResponseDto<CustomerMyPageResponseDto>> updateMyPage(@RequestHeader("customerName") String customerName, @RequestBody CustomerUpdateDto updateDto) {
        CustomerMyPageResponseDto getCustomerDto = customerService.updateMyPage(customerName, updateDto);
        String message = "회원 정보";
        return ApiResponseUtil.success(message, getCustomerDto, 200);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<String>> register(@RequestBody CustomerRequestDto requestDto) {
        logger.debug("register endpoint called with requestDto: {}", requestDto);
        customerService.register(requestDto);
        String message = "해당 메일주소로 확인 메일을 보냈습니다.";

        return ApiResponseUtil.success(message, requestDto.getEmail(), 200);
    }
}
