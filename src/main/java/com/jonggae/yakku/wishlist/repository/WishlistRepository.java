package com.jonggae.yakku.wishlist.repository;

import com.jonggae.yakku.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByCustomerId(Long customerId);

}
