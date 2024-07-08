package com.jonggae.yakku.wishlist.service;

import com.jonggae.yakku.common.exceptions.NotFoundProductException;
import com.jonggae.yakku.common.exceptions.NotFoundWishlistItemException;
import com.jonggae.yakku.products.entity.Product;
import com.jonggae.yakku.products.repository.ProductRepository;
import com.jonggae.yakku.wishlist.dto.WishlistItemDto;
import com.jonggae.yakku.wishlist.entity.WishlistItem;
import com.jonggae.yakku.wishlist.repository.WishlistItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;

    public List<WishlistItemDto> getWishlist(Long customerId) {
        List<WishlistItem> wishlistItemList = wishlistItemRepository.findByCustomerId(customerId);
        return wishlistItemList.stream()
                .map(WishlistItemDto::from)
                .collect(Collectors.toList());
    }

    //위시리스트에 상품 추가
    @Transactional
    public WishlistItemDto addWishlistItem(Long customerId, WishlistItemDto wishlistItemDto) {
        Product product = productRepository.findById(wishlistItemDto.getProductId())
                .orElseThrow(NotFoundProductException::new);
        WishlistItem existingItem = wishlistItemRepository.findByCustomerIdAndProductId(customerId, product.getId())
                .orElse(null);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + wishlistItemDto.getQuantity());
            return WishlistItemDto.from(wishlistItemRepository.save(existingItem));
        } else {
            WishlistItem newItem = WishlistItem.builder()
                    .customerId(customerId)
                    .product(product)
                    .quantity(wishlistItemDto.getQuantity())
                    .build();
            return WishlistItemDto.from(wishlistItemRepository.save(newItem));
        }
    }

    public WishlistItemDto updateWishlistItem(Long customerId, Long wishlistItemId, WishlistItemDto wishlistItemDto) {
        WishlistItem wishlistItem = wishlistItemRepository.findByIdAndCustomerId(wishlistItemId, customerId)
                .orElseThrow(NotFoundWishlistItemException::new);

        wishlistItem.setQuantity(wishlistItemDto.getQuantity());
        return WishlistItemDto.from(wishlistItemRepository.save(wishlistItem));
    }

    public void deleteWishlistItem(Long customerId, Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findByIdAndCustomerId(wishlistItemId, customerId)
                .orElseThrow(NotFoundWishlistItemException::new);
        wishlistItemRepository.delete(wishlistItem);
    }

    @Transactional
    public void clearWishlist(Long customerId) {
        wishlistItemRepository.deleteAllByCustomerId(customerId);
    }
}

