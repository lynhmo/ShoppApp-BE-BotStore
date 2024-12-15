package com.llu1ts.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderDetailRes extends BaseResponse {

    private Long id;

    @JsonProperty("order_id")
    private Long order;

    @JsonProperty("product_id")
    private Long product;

    private Float price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("color")
    private String color;
}
