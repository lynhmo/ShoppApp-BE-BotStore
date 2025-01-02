package com.llu1ts.shopapp.controller;


import com.llu1ts.shopapp.dto.OrderDTO;
import com.llu1ts.shopapp.dto.UpdateOrderStatus;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.SuccessResponse;
import com.llu1ts.shopapp.service.svc.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderCtrl {
    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO order,
                                         BindingResult bindingResult) throws DataNotFoundException {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.toString());
        }
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable long userId,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "0") int page) throws DataNotFoundException {
        return ResponseEntity.ok(orderService.getAllOrdersByUserId(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/user/detail/{userId}")
    public ResponseEntity<?> getOrdersAndDetailByUserId(@PathVariable long userId) throws DataNotFoundException {
        return ResponseEntity.ok(orderService.getAllOrderDetailByUserId(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderByOrderId(@PathVariable int orderId) throws DataNotFoundException {
        return ResponseEntity.ok(orderService.getOrderByOrderId(orderId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable int id,
                                         @Valid @RequestBody OrderDTO orderDto,
                                         BindingResult bindingResult) throws DataNotFoundException {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.toString());
        }
        return ResponseEntity.ok(orderService.updateOrder(id, orderDto));
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateOrderStatus(
            @RequestBody UpdateOrderStatus updateOrderStatus
    ) throws DataNotFoundException {
        orderService.updateOrderStatus(updateOrderStatus.getUserId(), updateOrderStatus.getOrderId(), updateOrderStatus.getOrderStatus());
        return ResponseEntity.ok(new SuccessResponse("Update order to " + updateOrderStatus.getOrderStatus(), "1", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) throws DataNotFoundException {

        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
