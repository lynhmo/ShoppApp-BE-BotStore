package com.llu1ts.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.llu1ts.shopapp.entity.ProductImage}
 */
@Value
@Builder
public class ProductImageDto implements Serializable {


    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's must be greater than 0")
    Long productId;

    @Size(message = "Image's name must be greater than 5 and lower than 200 character", min = 5, max = 200)
    String imageUrl;
}