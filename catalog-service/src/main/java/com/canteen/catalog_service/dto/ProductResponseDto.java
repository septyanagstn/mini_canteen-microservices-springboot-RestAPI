package com.canteen.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.canteen.catalog_service.entity.ProductStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private UUID id;
    private String sku;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status;
    private Instant createdAt;
}