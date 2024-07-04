package com.jonggae.yakku.products.dto;

import com.jonggae.yakku.products.entity.Product;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class ProductDto {

    private Long id;
    private String productName;
    private String productDescription;
    private Long price;
    private Long stock;
    private Long orderId; //kafka 메시지 처리 위해 추가

    public static ProductDto from(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
    public static ProductDto fromWithOrderId(Product product, Long orderId) {
        return ProductDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .orderId(orderId)
                .build();
    }

    public static Product toEntity(ProductDto productDto) {
        return Product.builder()
                .productName(productDto.getProductName())
                .productDescription(productDto.getProductDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .build();
    }
}
