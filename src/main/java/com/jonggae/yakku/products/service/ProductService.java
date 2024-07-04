package com.jonggae.yakku.products.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonggae.yakku.exceptions.NotFoundProductException;
import com.jonggae.yakku.products.dto.CustomPageDto;
import com.jonggae.yakku.products.dto.ProductDto;
import com.jonggae.yakku.products.dto.ProductInfoRequest;
import com.jonggae.yakku.products.entity.Product;
import com.jonggae.yakku.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {


    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "product-info-request", groupId = "product-service")
    public void handleProductRequests(String message){
        log.info("Received product info request: {}", message);

        try {
            ProductInfoRequest request =objectMapper.readValue(message, ProductInfoRequest.class);
            log.info("Parsed request: {}", request);
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(NotFoundProductException::new);
            log.info("Parsed request: {}", request);

            ProductDto productDto = ProductDto.fromWithOrderId(product, request.getOrderId());
            productDto.setOrderId(request.getOrderId());

            String response = objectMapper.writeValueAsString(productDto);
            log.info("Prepared response: {}", response);

            kafkaTemplate.send("product-info-response", String.valueOf(product.getId()), response);
        } catch (JsonProcessingException e) {
            log.error("Error processing product info request", e);

        }
    }

    //상품 단일 조회 (상세 조회)
    public ProductDto showProductInfo(Long productId) {
        return productRepository.findById(productId)
                .map(ProductDto::from)
                .orElseThrow(NotFoundProductException::new);
    }

    //상품 등록 todo : 누가 상품을 올리는 건지 모르겠는데, 권한을 지정해야하는지?
    public ProductDto addProduct(ProductDto productDto) {
        Product product = ProductDto.toEntity(productDto);
        return ProductDto.from(productRepository.save(product));
    }

    //전체 조회
    public CustomPageDto<ProductDto> showAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        Page<ProductDto> productDtoPage = productPage.map(ProductDto::from);

        return CustomPageDto.from(productDtoPage);
    }



    // 상품 정보 수정
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(NotFoundProductException::new);
        product.updateFromDto(productDto);
        return ProductDto.from(productRepository.save(product));
    }

    // 상품 삭제 -삭제 후 전체 목록 반환
    public CustomPageDto<ProductDto> deleteProduct(Long productId, int page, int size){
        Product product = productRepository.findById(productId)
                .orElseThrow(NotFoundProductException::new);
        productRepository.delete(product);
        return showAllProducts(page, size);
    }
}
