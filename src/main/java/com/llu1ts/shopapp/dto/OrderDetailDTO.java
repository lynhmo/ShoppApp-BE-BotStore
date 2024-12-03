package com.llu1ts.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderDetailDTO {

    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's ID must be greater than zero")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's ID must be greater than zero")
    private Long productId;

    @Min(value = 0, message = "Price must be greater than or equal to zero")
    private Long price;

    @JsonProperty("number_of_product")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be greater than or equal to zero")
    private int totalMoney;

    private String color;
}
