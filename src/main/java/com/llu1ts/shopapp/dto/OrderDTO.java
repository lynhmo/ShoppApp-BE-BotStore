package com.llu1ts.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OrderDTO {

    @JsonProperty("user_id")
    @Min(value = 1, message = "User's ID must differ than zero")
    private Long userId;

    @JsonProperty("fullname")
    private String fullname;

    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "username is require")
    private String phoneNumber;

    private String address;

    private String note;

    private String status;

    @JsonProperty("total_money")
//    @Min(value = 0, message = "Total money must greater or equal to zero")
    private Float totalMoney;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;
}

