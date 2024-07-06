package com.jonggae.yakku.wishlist.controller;

import com.jonggae.yakku.common.apiResponse.ApiResponseDto;
import com.jonggae.yakku.common.apiResponse.ApiResponseUtil;
import com.jonggae.yakku.wishlist.dto.WishlistItemDto;
import com.jonggae.yakku.wishlist.messages.WishlistApiMessages;
import com.jonggae.yakku.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistApiController {

    private final WishlistService wishlistService;
    private static final Logger logger = LoggerFactory.getLogger(WishlistApiController.class);

    private static final String ADD_SUCCESS_MESSAGE = WishlistApiMessages.WISHLIST_ITEM_ADD_SUCCESS;
    private static final String UPDATE_SUCCESS_MESSAGE = WishlistApiMessages.WISHLIST_ITEM_UPDATE_SUCCESS;
    private static final String DELETE_SUCCESS_MESSAGE = WishlistApiMessages.WISHLIST_ITEM_DELETE_SUCCESS;
    private static final String CLEAR_SUCCESS_MESSAGE = WishlistApiMessages.WISHLIST_CLEAR_SUCCESS;
    private static final String GET_SUCCESS_MESSAGE = WishlistApiMessages.WISHLIST_SHOW_SUCCESS;


    @GetMapping("/my-wishlist")
    public ResponseEntity<ApiResponseDto<List<WishlistItemDto>>> getWishlist(@RequestHeader("customerId") Long customerId) {
        logger.debug("getWishlist endpoint called with customerName: {}", customerId);
        List<WishlistItemDto> wishlistItems = wishlistService.getWishlist(customerId);
        return ApiResponseUtil.success(GET_SUCCESS_MESSAGE, wishlistItems, 200);
    }

    @PostMapping("/my-wishlist")
    public ResponseEntity<ApiResponseDto<WishlistItemDto>> addWishlistItem(
            @RequestHeader("customerId") Long customerId,
            @RequestBody WishlistItemDto wishlistItemDto) {
        WishlistItemDto updatedItemDto = wishlistService.addWishlistItem(customerId, wishlistItemDto);
        return ApiResponseUtil.success(ADD_SUCCESS_MESSAGE, updatedItemDto, 200);
    }

    // 위시리스트 수정
    @PatchMapping("/my-wishlist/{wishlistItemId}")
    public ResponseEntity<ApiResponseDto<WishlistItemDto>> updateWishlistItem(
            @RequestHeader("customerId") Long customerId,
            @PathVariable Long wishlistItemId,
            @RequestBody WishlistItemDto wishlistItemDto) {
        WishlistItemDto updatedItem = wishlistService.updateWishlistItem(customerId, wishlistItemId, wishlistItemDto);
        return ApiResponseUtil.success(UPDATE_SUCCESS_MESSAGE, updatedItem, HttpStatus.OK.value());
    }

    @DeleteMapping("/my-wishlist/{wishlistItemId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteWishlistItem(
            @RequestHeader("customerId") Long customerId,
            @PathVariable Long wishlistItemId) {
        wishlistService.deleteWishlistItem(customerId, wishlistItemId);
        return ApiResponseUtil.success(DELETE_SUCCESS_MESSAGE, null, HttpStatus.OK.value());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponseDto<Void>> clearWishlist(
            @RequestHeader("customerId") Long customerId) {
        wishlistService.clearWishlist(customerId);
        return ApiResponseUtil.success(CLEAR_SUCCESS_MESSAGE, null, HttpStatus.OK.value());
    }

}

