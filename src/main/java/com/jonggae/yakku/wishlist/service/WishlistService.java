package com.jonggae.yakku.wishlist.service;

import com.jonggae.yakku.wishlist.entity.Wishlist;
import com.jonggae.yakku.wishlist.entity.WishlistItem;
import com.jonggae.yakku.wishlist.repository.WishlistItemRepository;
import com.jonggae.yakku.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public List<WishlistItem> getWishlistForUser(Long customerId) {
        Optional<Wishlist> wishlist = wishlistRepository.findByCustomerId(customerId);
        return wishlist.map(value -> wishlistItemRepository.findByWishlistId(value.getId())).orElse(null);
    }

    public WishlistItem addItemToWishlist(Long customerId, WishlistItem item) {
        Optional<Wishlist> wishlist = wishlistRepository.findByCustomerId(customerId);
        if (wishlist.isPresent()) {
            item.setWishlist(wishlist.get());
            return wishlistItemRepository.save(item);
        } else {
            // 위시리스트가 없는 경우 예외 처리
            throw new RuntimeException("Wishlist not found for customer id: " + customerId);
        }
    }
}


//    private final WishlistRepository wishlistRepository;
//    private final WishlistItemRepository wishlistItemRepository;
//    private final ProductRepository productRepository;
//    private final CustomerRepository customerRepository;
//
//
//    //내 위시리스트 조회
//    public WishlistDto getWishlistItems(Long customerId) {
//        Customer customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new NotFoundMemberException("Customer not found"));
//
//        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
//                .orElseGet(() -> wishlistRepository.save(Wishlist.builder()
//                        .customer(customer)
//                        .wishlistItemList(new ArrayList<>())
//                        .build()));
//
//        return WishlistDto.from(wishlist);
//    }
//
//// 항목 추가
//
//    public WishlistItemDto addWishItem(Long customerId, WishlistItemDto wishlistItemDto) {
//        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
//                .orElseGet(() -> wishlistRepository.save(new Wishlist(customerId)));
//
//        Product product = productRepository.findById(wishlistItemDto.getProductId())
//                .orElseThrow(NotFoundProductException::new);
//
//        WishlistItem addedItem = WishlistItem.builder()
//                .wishlist(wishlist)
//                .product(product)
//                .quantity(wishlistItemDto.getQuantity())
//                .build();
//        return WishlistItemDto.from(wishlistItemRepository.save(addedItem));
//    }
//
//    //위시리스트 항목 수량 수정
//    public WishlistDto updateWishItem(Long customerId, Long itemId, WishlistItemDto wishlistItemDto) {
//        WishlistItem wishlistItem = wishlistItemRepository.findById(itemId)
//                .orElseThrow(NotFoundWishlistItemException::new);
//
//        wishlistItem.setQuantity(wishlistItemDto.getQuantity());
//        wishlistItemRepository.save(wishlistItem);
//        return getWishlistItems(customerId);
//    }
//
//    //항목 삭제
//    public WishlistDto deleteWishItem(Long customerId, Long itemId) {
//        WishlistItem wishlistItem = wishlistItemRepository.findById(itemId)
//                .orElseThrow(NotFoundWishlistItemException::new);
//        wishlistItemRepository.deleteById(wishlistItem.getId());
//        return getWishlistItems(customerId);
//    }
//
//    //전체 비우기
//    @Transactional
//    public void clearWishlist(Long customerId) {
//        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
//                .orElseThrow(NotFoundWishlistException::new);
//        wishlistItemRepository.deleteAllByWishlist(wishlist);
//        WishlistDto.from(wishlist);
//    }


