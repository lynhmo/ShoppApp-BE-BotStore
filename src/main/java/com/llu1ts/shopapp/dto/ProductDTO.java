package com.llu1ts.shopapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ProductDTO {
    @NotBlank
    @Size(min = 5, max = 200, message = "Title must be between 3 to 200 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than 0 or equal to 0")
    @Max(value = 100000000, message = "Price must be lower than 100000000 or equal to 100000000")
    private Float price;

    private MultipartFile thumbnail;

    private String description;

    @JsonProperty("category_id")
    private Long categoryId;
}
