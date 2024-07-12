package com.jonggae.yakku.products.controller;

import com.jonggae.yakku.products.dto.StockReservationRequestDto;
import com.jonggae.yakku.products.dto.UpdateStockRequestDto;
import com.jonggae.yakku.products.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//재고만을 관리하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class StockController {

    private final StockService stockService;

    @PostMapping("/{productId}/update-stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long productId, @RequestBody UpdateStockRequestDto request) {
        stockService.updateStock(productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/initialize-stock")
    public ResponseEntity<Void> initializeAllStock() {
        stockService.loadInitialStock();
        return ResponseEntity.ok().build();
    }
    //재고 확인
    @GetMapping("/{productId}/stock")
    public ResponseEntity<Long> checkStock(@PathVariable Long productId) {
        Long stock = stockService.getStock(productId);
        return ResponseEntity.ok(stock);
    }

    //재고 예약
    @PostMapping("/{productId}/reserve-stock")
    public ResponseEntity<Boolean> reserveStock(@PathVariable Long productId, @RequestBody StockReservationRequestDto request) {
        boolean reserved = stockService.reserveStock(productId, request.getQuantity());
        return ResponseEntity.ok(reserved);
    }
    //예약된 재고에 대한 주문 확정->재고 감소
    @PostMapping("/{productId}/confirm-reservation")
    public ResponseEntity<Void> confirmReservation(@PathVariable Long productId, @RequestBody StockReservationRequestDto request) {
        stockService.confirmReservation(productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // 취소 -> 재고 회복
    @PostMapping("/{productId}/cancel-reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long productId, @RequestBody StockReservationRequestDto request) {
        stockService.cancelReservation(productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

}
