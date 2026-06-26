package com.canteen.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotBlank(message = "Nama customer tidak boleh kosong")
    private String customerName;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String customerEmail;

    @NotEmpty(message = "Item pesanan minimal 1")
    @Valid
    private List<OrderItemRequestDto> items;
}