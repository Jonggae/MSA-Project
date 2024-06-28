package com.jonggae.apigateway.exceptions;


import com.jonggae.apigateway.common.apiResponse.ApiResponseDto;
import com.jonggae.apigateway.common.apiResponse.ApiResponseUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Customer - 해당유저를 찾을 수 없을 때
    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleNotFoundMemberException(NotFoundMemberException ex) {
        String errorMessage = "해당 사용자를 찾을 수 없습니다";
        return ApiResponseUtil.error(errorMessage, 404, "NOT_FOUND_MEMBER", null);
    }

    // Product - 상품 등록 시 같은 이름의 상품을 등록하였을 때 - 데이터 무결성 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String errorMessage = "같은 상품 이름으로 등록할 수 없습니다.";
        return ApiResponseUtil.error(errorMessage, 400, "DATA_INTEGRITY_VIOLATION", null);
    }


}
