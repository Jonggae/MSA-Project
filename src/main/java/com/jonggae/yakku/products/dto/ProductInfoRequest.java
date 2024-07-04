package com.jonggae.yakku.products.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ProductInfoRequest {
    private Long productId;
    private Long customerId;
    private Long orderId;
}
