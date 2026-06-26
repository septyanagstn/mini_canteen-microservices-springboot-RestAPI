package com.canteen.order_service.service.impl;

import com.canteen.order_service.dto.OrderItemRequestDto;
import com.canteen.order_service.dto.OrderItemResponseDto;
import com.canteen.order_service.dto.OrderRequestDto;
import com.canteen.order_service.dto.OrderResponseDto;
import com.canteen.order_service.entity.Order;
import com.canteen.order_service.entity.OrderItem;
import com.canteen.order_service.entity.OrderStatus;
import com.canteen.order_service.repository.OrderRepository;
import com.canteen.order_service.service.OrderService;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestClient restClient;

    // constructor
    public OrderServiceImpl(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
        // mendefinisikan RestClient untuk komunikasi dengan catalog-service;
        this.restClient = RestClient.builder()
                        .baseUrl("http://localhost:8081/api/products")
                        .build();
    }
    
    // Versi Docker
    // public OrderServiceImpl(OrderRepository orderRepository,
    //         RestClient.Builder restClientBuilder,
    //         @Value("${catalog.service.url:http://localhost:8081}") String catalogUrl) {
    //     this.orderRepository = orderRepository;
    //     // mendefinisikan RestClient untuk komunikasi dengan catalog-service;
    //     this.restClient = restClientBuilder.baseUrl(catalogUrl).build();
    // }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        try {
            for (OrderItemRequestDto itemReq : request.getItems()) {

                // mengambil data product dari catalog-service untuk snapshot data produk yang
                // di order
                Map<String, Object> productResponse = restClient.get()
                        .uri("/{id}", itemReq.getProductId())
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {
                        });

                if (productResponse == null) {
                    throw new RuntimeException("Produk dengan ID " + itemReq.getProductId() + " tidak ditemukan");
                }

                // berdasarkan data snapshot dari catalog-service, kemudian stok di perbarui
                restClient.patch()
                        .uri("/{id}/stock?quantity={qty}", itemReq.getProductId(), itemReq.getQuantity())
                        .retrieve()
                        .toBodilessEntity();

                // data snapshot disimpan ke order item untuk rekap item yang diorder
                OrderItem item = new OrderItem();
                item.setProductId(itemReq.getProductId());
                item.setQuantity(itemReq.getQuantity());
                item.setProductSku((String) productResponse.get("sku"));
                item.setProductName((String) productResponse.get("name"));
                BigDecimal price = new BigDecimal(productResponse.get("price").toString());
                item.setProductPrice(price);
                item.setOrder(order);

                items.add(item);

                // menghitung total harga untuk satu order (bisa banyak item)
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            }

            order.setItems(items);
            order.setTotalAmount(totalAmount);

            // setelah semuanya terisi, order di simpan ke db
            Order savedOrder = orderRepository.save(order);

            // mengembalikan response order yang dibuat
            return mapToResponse(savedOrder);

        } catch (Exception e) {
            throw new RuntimeException("Gagal memproses pembuatan pesanan: " + e.getMessage());
        }
    }

    @Override
    public OrderResponseDto getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDto payOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pesanan dengan ID " + id + " tidak ditemukan"));

        // Aturan Bisnis: Hanya pesanan berstatus PENDING yang bisa dibayar
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Pesanan tidak dapat dibayar karena status saat ini adalah " + order.getStatus());
        }

        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pesanan dengan ID " + id + " tidak ditemukan"));

        // jika status order sudah dibayar, tidak bisa dicancel
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Pesanan tidak dapat dibatalkan karena status saat ini adalah " + order.getStatus());
        }

        // mengembalikan stok yang sudah dikurangi saat order, dengan mengirim nilai
        // quantity negatif karena fungsi updateStock
        // di catalog-service melakukan pengurangan stok
        for (OrderItem item : order.getItems()) {
            try {
                restClient.patch()
                        .uri("/{id}/stock?quantity={qty}", item.getProductId(), -item.getQuantity())
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Gagal mengembalikan stok untuk produk " + item.getProductName() + " saat pembatalan pesanan.");
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    // fungsi ini digunakan untuk memetakan hasil query db ke dto agar tidak
    // menggunakan entity secara langsung di response
    private OrderResponseDto mapToResponse(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemResponseDto itemDto = new OrderItemResponseDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProductId());
                itemDto.setProductSku(item.getProductSku());
                itemDto.setProductName(item.getProductName());
                itemDto.setProductPrice(item.getProductPrice());
                itemDto.setQuantity(item.getQuantity());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}