package com.canteen.catalog_service.service;

import com.canteen.catalog_service.dto.ProductRequestDto;
import com.canteen.catalog_service.dto.ProductResponseDto;

import java.util.List;
import java.util.UUID;

public interface CatalogService {
    ProductResponseDto createProduct(ProductRequestDto request);
    List<ProductResponseDto> getAllProducts(Integer page, Integer size);
    ProductResponseDto getProductById(UUID id);
    void updateStock(UUID id, Integer quantity);
    void updateStatus(UUID id, String status);
}