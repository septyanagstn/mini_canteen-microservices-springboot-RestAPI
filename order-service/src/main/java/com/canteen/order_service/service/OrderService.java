package com.canteen.order_service.service;

import com.canteen.order_service.dto.OrderRequestDto;
import com.canteen.order_service.dto.OrderResponseDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto request);
    OrderResponseDto getOrderById(UUID id);
    OrderResponseDto payOrder(UUID id);
    OrderResponseDto cancelOrder(UUID id);
    List<OrderResponseDto> getAllOrders(Integer page, Integer size);
}