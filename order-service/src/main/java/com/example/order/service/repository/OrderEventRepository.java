package com.example.order.service.repository;

import com.example.order.service.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventRepository extends JpaRepository<OrderEvent, String> {
}
