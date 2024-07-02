package com.jonggae.yakku.wishlist.controller;

import com.jonggae.yakku.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

//    @GetMapping
//    public ResponseEntity<List<WishlistItem>> getWishlist() {
//        return SecurityUtil.getCurrentUser()
//                .map(user -> ResponseEntity.ok(wishlistService.getWishlistForUser(user.getId())))
//                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//    }
//
//    @PostMapping
//    public ResponseEntity<WishlistItem> addWishlistItem(@RequestBody WishlistItem item) {
//        return SecurityUtil.getCurrentUser()
//                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.addItemToWishlist(user.getId(), item)))
//                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
//    }

//
//    private final WishlistService wishlistService;
//    private final SecurityUtil securityUtil;
//
//    @GetMapping("/my-wishlist")
//    public ResponseEntity<ApiResponseDto<WishlistDto>> getWishlistItems() {
//        Long customerId = securityUtil.getCurrentCustomerId();
//        WishlistDto wishlistDto = wishlistService.getWishlistItems(customerId);
//        String message = MessageUtil.getMessage(WishlistApiMessages.WISHLIST_SHOW_SUCCESS);
//        return ApiResponseUtil.success(message, wishlistDto, 200);
//    }
//
//    @PostMapping("/my-wishlist/items")
//    public ResponseEntity<ApiResponseDto<WishlistItemDto>> addWishItem(@RequestBody WishlistItemDto wishlistItemDto) {
//        Long customerId = securityUtil.getCurrentCustomerId();
//        WishlistItemDto updatedItemDto = wishlistService.addWishItem(customerId, wishlistItemDto);
//        String message = MessageUtil.getMessage(WishlistApiMessages.WISHLIST_ITEM_ADD_SUCCESS);
//        return ApiResponseUtil.success(message, updatedItemDto, 200);
//    }
//
//    @PatchMapping("/my-wishlist/items/{itemId}")
//    public ResponseEntity<ApiResponseDto<WishlistDto>> updateWishlistItem(@PathVariable(name = "itemId") Long itemId,
//                                                                          @RequestBody WishlistItemDto wishlistItemDto) {
//        Long customerId = securityUtil.getCurrentCustomerId();
//        WishlistDto updatedWishlist = wishlistService.updateWishItem(customerId, itemId, wishlistItemDto);
//        String message = MessageUtil.getMessage(WishlistApiMessages.WISHLIST_ITEM_UPDATE_SUCCESS);
//        return ApiResponseUtil.success(message, updatedWishlist, 200);
//    }
//
//    @DeleteMapping("/my-wishlist/items/{itemId}")
//    public ResponseEntity<ApiResponseDto<WishlistDto>> deleteWishlistItem(@PathVariable(name = "itemId") Long itemId) {
//        Long customerId = securityUtil.getCurrentCustomerId();
//        WishlistDto updatedWishlist = wishlistService.deleteWishItem(customerId, itemId);
//        String message = MessageUtil.getMessage(WishlistApiMessages.WISHLIST_ITEM_DELETE_SUCCESS);
//        return ApiResponseUtil.success(message, updatedWishlist, 200);
//    }
//
//    @DeleteMapping("/my-wishlist")
//    public ResponseEntity<ApiResponseDto<String>> clearCart() {
//        Long customerId = securityUtil.getCurrentCustomerId();
//        wishlistService.clearWishlist(customerId);
//        String message = MessageUtil.getMessage(WishlistApiMessages.WISHLIST_CLEAR_SUCCESS);
//        return ApiResponseUtil.success(message, null, 200);
//    }

}
