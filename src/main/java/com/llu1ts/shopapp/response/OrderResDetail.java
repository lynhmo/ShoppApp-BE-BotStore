package com.llu1ts.shopapp.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class OrderResDetail {
    private Long orderId;

    private Long userId;

    private String fullname;

    private String email;

    private String phoneNumber;

    private String address;

    private String note;

    private Date orderDate;

    private String status;

    private Float totalMoney;

    private String shippingMethod;

    private String shippingAddress;

    private LocalDate shippingDate;

    private String trackingNumber;

    private String paymentMethod;

    private Boolean active;

    private List<OrderDetailWithProductImage> orderDetails;
}
