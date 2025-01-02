package com.llu1ts.shopapp.response;

import lombok.Data;

@Data
public class OrderDetailWithProductImage {

    private ProductResponseImage product;

    private Float price;

    private int numberOfProducts;

    private Float totalMoney;

}
