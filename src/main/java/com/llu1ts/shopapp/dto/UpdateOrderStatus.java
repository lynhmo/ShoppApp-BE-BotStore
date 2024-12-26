package com.llu1ts.shopapp.dto;

import lombok.Data;

@Data
public class UpdateOrderStatus {
    long orderId;
    long userId;
    String orderStatus;

}
