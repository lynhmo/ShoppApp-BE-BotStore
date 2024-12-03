package com.llu1ts.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is require")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
