package com.canteen.catalog_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    @NotBlank(message = "SKU tidak boleh kosong")
    private String sku;

    @NotBlank(message = "Nama produk tidak boleh kosong")
    private String name;

    @Positive(message = "Harga harus lebih dari 0")
    private BigDecimal price;

    @PositiveOrZero(message = "Stok tidak boleh negatif")
    private Integer stock;
}