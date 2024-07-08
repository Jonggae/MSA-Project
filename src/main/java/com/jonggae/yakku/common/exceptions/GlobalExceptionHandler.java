package com.jonggae.yakku.common.exceptions;

import com.jonggae.yakku.common.apiResponse.ApiResponseDto;
import com.jonggae.yakku.common.apiResponse.ApiResponseUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // Product - 상품 등록 시 같은 이름의 상품을 등록하였을 때 - 데이터 무결성 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String errorMessage = "같은 상품 이름으로 등록할 수 없습니다.";
        return ApiResponseUtil.error(
                errorMessage,
                400,
                "DATA_INTEGRITY_VIOLATION",
                null
        );
    }

    // Product - 찾으려는 상품이 없을 때 (404)
    @ExceptionHandler(NotFoundProductException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleNotFoundProductException(NotFoundProductException ex) {
        String errorMessage = ex.getMessage();
        return ApiResponseUtil.error(
                errorMessage,
                404,
                "NOT_FOUND_PRODUCT",
                null
        );
    }

    // Order - 재고가 충분하지 않을 때 400
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleInsufficientStockException(InsufficientStockException ex) {
        String errorMessage = ex.getMessage();
        return ApiResponseUtil.error(
                errorMessage,
                400,
                "INSUFFICIENT_STOCK",
                null);
    }
}
