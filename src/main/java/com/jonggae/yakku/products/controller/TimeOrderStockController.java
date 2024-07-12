package com.jonggae.yakku.products.controller;

import com.jonggae.yakku.products.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class TimeOrderStockController {

    private final StockService stockService;
    @PostMapping("/{productId}/reserve")
    public ResponseEntity<Boolean> reserveStock(
            @PathVariable Long productId,
            @RequestParam Long quantity) {
        boolean reserved = stockService.reserveStock(productId, quantity);
        return ResponseEntity.ok(reserved);
    }

    @PostMapping("/{productId}/confirm")
    public ResponseEntity<Void> confirmStockReservation(
            @PathVariable Long productId,
            @RequestParam Long quantity) {
        stockService.confirmReservation(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/cancel")
    public ResponseEntity<Void> cancelStockReservation(
            @PathVariable Long productId,
            @RequestParam Long quantity) {
        stockService.cancelReservation(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
