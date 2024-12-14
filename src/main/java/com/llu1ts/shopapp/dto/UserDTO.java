package com.llu1ts.shopapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @JsonProperty("fullname")
    String fullName;

    @JsonProperty("username")
    @NotBlank(message = "username is require")
    String phoneNumber;

    String address;

    @NotBlank(message = "Password cannot be blank")
    String password;

    @NotBlank(message = "Confirm password cannot be blank")
    @JsonProperty("retypePassword")
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
