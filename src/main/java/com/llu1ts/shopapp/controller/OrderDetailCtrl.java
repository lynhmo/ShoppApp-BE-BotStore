package com.llu1ts.shopapp.controller;


import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDetailDTO;
import com.llu1ts.shopapp.entity.Order;
import com.llu1ts.shopapp.entity.OrderDetail;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.OrderDetailRes;
import com.llu1ts.shopapp.response.OrderDetailWithProductImage;
import com.llu1ts.shopapp.response.SuccessResponse;
import com.llu1ts.shopapp.service.svc.OrderDetailService;
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
@RequestMapping("/api/v1/order-details")
@RequiredArgsConstructor
public class OrderDetailCtrl {

    private final OrderDetailService orderDetailService;

    @PostMapping
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.toString());
        }
        return ResponseEntity.ok().body(orderDetailService.createOrderDetail(orderDetailDTO));
    }


    @GetMapping("/{detailOrderId}")
    public ResponseEntity<?> getOrderDetailById(@PathVariable Long detailOrderId) throws DataNotFoundException {
        return ResponseEntity.ok(orderDetailService.getOrderDetail(detailOrderId));
    }

    @GetMapping("/order/{orderID}")
    public ResponseEntity<ApiPageResponse<OrderDetailRes>> getOrdersDetailByOrderID(@PathVariable Long orderID,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) throws DataNotFoundException {
        return ResponseEntity.ok(orderDetailService.getAllOrderDetailByOrderId(orderID, PageRequest.of(page, size)));
    }


    @GetMapping("/order/admin/{orderID}")
    public ResponseEntity<List<OrderDetailWithProductImage>> apiPageResponseResponseEntity(@PathVariable Long orderID) throws DataNotFoundException {
        return ResponseEntity.ok(orderDetailService.getAllOrderDetailWithProductAdmin(orderID));
    }



    @PutMapping("/{detailOrderId}")
    public ResponseEntity<?> updateOrderDetail(
            @PathVariable Long detailOrderId,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderDetailService.updateOrderDetail(detailOrderId, orderDetailDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteOrderDetail(@PathVariable Long id) throws Exception {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok().body(new SuccessResponse("Deleted order detail", "1", null));
    }

    @DeleteMapping("/order/{orderID}")
    public ResponseEntity<SuccessResponse> deleteAllOrderDetail(@PathVariable Long orderID) throws DataNotFoundException {
        orderDetailService.deleteAllOrderDetail(orderID);
        return ResponseEntity.ok(new SuccessResponse("Deleted all order detail", "1", null));
    }
}
