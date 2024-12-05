package com.llu1ts.shopapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.util.Date;

/**
 * DTO for {@link com.llu1ts.shopapp.entity.User}
 */

@Value
public class UserDTO {

    @JsonProperty("fullname")
    String fullName;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is require")
    String phoneNumber;

    String address;

    @NotBlank(message = "Password cannot be blank")
    String password;

    @NotBlank(message = "Confirm password cannot be blank")
    @JsonProperty("retype_password")
    String retypePassword;

    @JsonProperty("date_of_birth")
    Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    Long facebookAccountId;

    @JsonProperty("google_account_id")
    Long googleAccountId;

    @JsonProperty("role_id")
    Long roleId;
}
