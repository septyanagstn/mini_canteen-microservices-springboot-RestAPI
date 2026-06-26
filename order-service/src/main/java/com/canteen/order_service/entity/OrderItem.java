package com.canteen.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID productId; 

    @Column(nullable = false)
    private String productSku;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal productPrice; 

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}