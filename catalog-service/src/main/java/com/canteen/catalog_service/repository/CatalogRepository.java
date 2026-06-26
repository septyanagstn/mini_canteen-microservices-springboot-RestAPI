package com.canteen.catalog_service.repository;

import com.canteen.catalog_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// karena menggunakan JPA Repository tidak perlu lagi menambahkan @Repository
public interface CatalogRepository extends JpaRepository<Product, UUID> {
    // untuk kebutuhan pembuatan produk baru, agar unik SKUnya
    Optional<Product> findBySku(String sku);
}