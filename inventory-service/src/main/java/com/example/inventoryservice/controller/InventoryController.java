package com.example.inventoryservice.controller;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.service.InventoryService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Product>> getFullInventory() {
        var data = inventoryService.getFullInventory();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") String id) {
        var data = inventoryService.getProductFromInventory(id);
        if (data == null) {
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        } else if (data.getQuantity() <= 0) {
            System.out.println("Product has no items");
            return new ResponseEntity<>(data, HttpStatus.FOUND);
        }
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        product.setProductId(UUID.randomUUID().toString());
        var created = inventoryService.createProduct(product);
        return ResponseEntity.ok(created);
    }

    @PutMapping("add/{id}/{add}")
    public ResponseEntity<Product> addProductQuantity(
            @PathVariable("id") String id,
            @PathVariable("add") Long addQuantity) {
        Product curRecord = inventoryService.getProductFromInventory(id);
        if (curRecord == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        curRecord.setQuantity(curRecord.getQuantity() + addQuantity);
        inventoryService.updateProduct(curRecord);
        return new ResponseEntity<>(curRecord, HttpStatus.OK);
    }

    @PutMapping("sub/{id}/{sub}")
    public ResponseEntity<Product> subProductQuantity(
            @PathVariable("id") String id,
            @PathVariable("sub") Long quantity) {
        Product curRecord = inventoryService.getProductFromInventory(id);
        if (curRecord == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        curRecord.setQuantity(curRecord.getQuantity() - quantity);
        inventoryService.updateProduct(curRecord);
        return new ResponseEntity<>(curRecord, HttpStatus.OK);
    }

}
