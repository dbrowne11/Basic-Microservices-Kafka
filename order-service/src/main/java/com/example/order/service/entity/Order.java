package com.example.order.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(name="orders")
public class Order implements Serializable {
    @Id
    private String orderId;
    private String itemName;
    private String productId;
    private Long quantity;
    private Double price;
    private Date orderDate;
    private String status;

}
