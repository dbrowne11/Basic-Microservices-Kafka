package com.example.inventoryservice.payload;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Order {
    private String orderId;
    private String itemName;
    private String productId;
    private Long quantity;
    private Double price;
    private Date orderDate;
    private String status;
}
