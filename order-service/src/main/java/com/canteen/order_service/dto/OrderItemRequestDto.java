package com.canteen.order_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemRequestDto {
    @NotNull(message = "Product ID wajib diisi")
    private UUID productId;

    @Positive(message = "Item pesanan minimal 1")
    private Integer quantity;
}