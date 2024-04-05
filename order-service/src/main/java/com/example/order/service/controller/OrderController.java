package com.example.order.service.controller;

import com.example.order.service.entity.Order;
import com.example.order.service.entity.OrderEvent;
import com.example.order.service.service.OrderProducer;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<OrderEvent> placeOrder(@RequestBody Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        orderProducer.saveOrder(order);
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderRef(order);
        orderEvent.setStatus("PENDING");
        orderEvent.setEventId(UUID.randomUUID().toString());
        orderProducer.sendMessage(orderEvent);

        return ResponseEntity.ok(orderEvent);
    }

    @GetMapping
    public ResponseEntity<List<OrderEvent>> getOrders() {
        List<OrderEvent> orders = orderProducer.getAllOrderEvents();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEvent> getOrderById(@PathVariable("id") String id) {
        OrderEvent order = orderProducer.getOrderById(id);
        if (order == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/status/{id}/{status}")
    public ResponseEntity<OrderEvent> updateOrderStatus(@PathVariable("id") String id, @PathVariable("status") String status) {
        OrderEvent thisOrder = orderProducer.getOrderById(id);

        OrderEvent updated = orderProducer.updateStatus(thisOrder, status);
        return new ResponseEntity<>(updated, HttpStatus.ACCEPTED);
    }

}
