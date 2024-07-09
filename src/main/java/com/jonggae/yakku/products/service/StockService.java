package com.jonggae.yakku.products.service;

import com.jonggae.yakku.products.entity.Product;
import com.jonggae.yakku.products.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final RedisTemplate<String, Long> redisTemplate;
    private final ProductRepository productRepository;

    public void initializeStock(Long productId, Long quantity){
        String key = "stock:" + productId;
        redisTemplate.opsForValue().set(key, quantity);
    }

    public void updateStock(Long productId, Long quantity){
        String key = "stock:" + productId;
        redisTemplate.opsForValue().set(key, quantity);
    }

    @PostConstruct
    public void loadInitialStock() {
        List<Product> products = productRepository.findAll();
        for(Product product : products){
            initializeStock(product.getId(), product.getStock());
        }
    }

    public Long getStock(Long productId){
        String key = "stock:" + productId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse(0L);
    }

    public boolean reserveStock(Long productId, Long quantity) {
        String key = "stock:" + productId;
        return Boolean.TRUE.equals(redisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                Long currentStock = Optional.ofNullable((Long) operations.opsForValue().get(key)).orElse(0L);
                if (currentStock >= quantity) {
                    operations.multi();
                    operations.opsForValue().decrement(key, quantity);
                    return !operations.exec().isEmpty();
                }
                return false;
            }
        }));
    }

    public void confirmReservation(Long productId,  Long quantity){

    }
    public void cancelReservation(Long productId, Long quantity){
        String key = "stock:" + productId;
        redisTemplate.opsForValue().increment(key, quantity);
    }

}
