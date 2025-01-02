package com.llu1ts.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;


@Data
public class UserUpdateDTO {
    @JsonProperty("fullname")
    String fullName;

    String address;

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;

    @JsonProperty("role_id")
    Long roleId;
}
