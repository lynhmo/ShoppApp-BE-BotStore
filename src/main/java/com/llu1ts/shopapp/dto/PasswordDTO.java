package com.llu1ts.shopapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordDTO {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}
