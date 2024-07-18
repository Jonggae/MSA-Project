package com.jonggae.yakku.wishlist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @Column(name = "wishlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(name = "customer_id")
    private Long customerId;

    @OneToMany(mappedBy = "wishlist")
    private List<WishlistItem> wishlistItemList = new ArrayList<>();


    public Wishlist(Long customerId) {
        this.customerId = customerId;
    }
}
