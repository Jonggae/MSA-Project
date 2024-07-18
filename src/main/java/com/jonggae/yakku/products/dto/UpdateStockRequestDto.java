package com.jonggae.yakku.products.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateStockRequestDto {
    private Long quantity;
}
