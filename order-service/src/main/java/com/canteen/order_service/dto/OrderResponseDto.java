package com.canteen.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.canteen.order_service.entity.OrderStatus;

@Data
public class OrderResponseDto {
    private UUID id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Instant createdAt;
    private List<OrderItemResponseDto> items;
}