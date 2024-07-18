package com.jonggae.yakku.wishlist.repository;

import com.jonggae.yakku.wishlist.entity.Wishlist;
import com.jonggae.yakku.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByWishlistId(Long wishlistId);
    List<WishlistItem> findByCustomerId(Long customerId);

    void deleteAllByCustomerId(Long customerId);

    Optional<WishlistItem> findByCustomerIdAndProductId(Long customerId, long id);

    Optional<WishlistItem> findByIdAndCustomerId(Long itemId, Long customerId);
}
