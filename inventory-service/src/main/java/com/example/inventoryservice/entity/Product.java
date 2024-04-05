package com.example.inventoryservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name="products"
)
public class Product
{
    @Id
    private String productId;
    private String productName;
    private Double price;
    private Long quantity;
}
