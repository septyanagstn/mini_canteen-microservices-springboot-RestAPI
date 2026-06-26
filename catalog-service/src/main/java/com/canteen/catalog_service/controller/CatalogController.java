package com.canteen.catalog_service.controller;

import com.canteen.catalog_service.dto.ProductRequestDto;
import com.canteen.catalog_service.dto.ProductResponseDto;
import com.canteen.catalog_service.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class CatalogController {

    private final CatalogService catalogService;

    // constructor
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(catalogService.getAllProducts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable UUID id, @RequestParam Integer quantity) {
        catalogService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        catalogService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}