package com.canteen.order_service.repository;

import com.canteen.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// karena menggunakan JPA Repository tidak perlu lagi menambahkan @Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}