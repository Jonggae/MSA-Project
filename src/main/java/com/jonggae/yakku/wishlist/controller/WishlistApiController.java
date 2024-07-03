package com.jonggae.yakku.wishlist.controller;

import com.jonggae.yakku.common.apiResponse.ApiResponseDto;
import com.jonggae.yakku.common.apiResponse.ApiResponseUtil;
import com.jonggae.yakku.wishlist.dto.WishlistItemDto;
import com.jonggae.yakku.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistApiController {

    private final WishlistService wishlistService;
    private static final Logger logger = LoggerFactory.getLogger(WishlistApiController.class);


    @GetMapping("/my-wishlist")
    public ResponseEntity<ApiResponseDto<List<WishlistItemDto>>> getWishlist(@RequestHeader("customerId") Long customerId) {
        logger.debug("getWishlist endpoint called with customerName: {}", customerId);
        List<WishlistItemDto> wishlistItems = wishlistService.getWishlist(customerId);
        String message = "위시리스트 조회 완료";
        return ApiResponseUtil.success(message, wishlistItems, 200);
    }
}

