package com.canteen.catalog_service.service.impl;

import com.canteen.catalog_service.dto.ProductRequestDto;
import com.canteen.catalog_service.dto.ProductResponseDto;
import com.canteen.catalog_service.entity.Product;
import com.canteen.catalog_service.entity.ProductStatus;
import com.canteen.catalog_service.repository.CatalogRepository;
import com.canteen.catalog_service.service.CatalogService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository repository;

    // constructor
    public CatalogServiceImpl(CatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    // Sebelum membuat produk baru, akan dilakukan pengecekan agar tidak ada SKU yang duplikat
    public ProductResponseDto createProduct(ProductRequestDto request) {
        if (repository.findBySku(request.getSku()).isPresent()) {
            throw new RuntimeException("Produk dengan SKU tersebut sudah ada");
        }

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product savedProduct = repository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponseDto> getAllProducts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getProductById(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public void updateStock(UUID id, Integer quantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        if (ProductStatus.INACTIVE.equals(product.getStatus())) {
            throw new RuntimeException("Produk tidak aktif (INACTIVE) dan tidak dapat dipesan");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stok tidak mencukupi. Diminta: " + quantity + ", Tersedia: " + product.getStock());
        }

        product.setStock(product.getStock() - quantity);
        repository.save(product);
    }

    @Override
    @Transactional
    public void updateStatus(UUID id, String status) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        product.setStatus(ProductStatus.valueOf(status.toUpperCase()));
        repository.save(product);
    }

    // fungsi ini digunakan untuk memetakan hasil query db ke dto agar tidak menggunakan entity secara langsung di response
    private ProductResponseDto mapToResponse(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}