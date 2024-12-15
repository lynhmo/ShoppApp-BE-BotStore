package com.llu1ts.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.llu1ts.shopapp.entity.Product;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ProductRes extends BaseResponse {
    private Long id;
    private String name;

    private Float price;

    private byte[] thumbnail;

    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductRes fromProduct(Product product, ProductRes productRes) {
        BeanUtils.copyProperties(product, productRes);
        productRes.setCategoryId(product.getCategory().getId());
        return productRes;
    }
}
