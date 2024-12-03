package com.llu1ts.shopapp.controller;


import com.llu1ts.shopapp.dto.OrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order-details")
public class OrderDetailCtrl {


    @PostMapping
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errors.toString());
            }
            return ResponseEntity.ok().body(orderDetailDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailById(@PathVariable Long id) {
        return ResponseEntity.ok("order detail found");
    }

    @GetMapping("/order/{orderID}")
    public ResponseEntity<List<?>> getOrdersDetailByOrderID(@PathVariable Long orderID) {
        return ResponseEntity.ok(new ArrayList<OrderDetailDTO>());
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @PathVariable Long id,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok().body(orderDetailDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok().body("order detail deleted");
    }


}
