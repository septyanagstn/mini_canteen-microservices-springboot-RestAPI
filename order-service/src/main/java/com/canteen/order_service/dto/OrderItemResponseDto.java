package com.canteen.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponseDto {
    private UUID id;
    private UUID productId;
    private String productSku;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
}