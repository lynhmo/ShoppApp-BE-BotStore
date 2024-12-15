package com.llu1ts.shopapp.response;

import com.llu1ts.shopapp.entity.Role;
import lombok.Data;

import java.util.Date;


@Data
public class UserResponse {
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String address;

    private Boolean isActive;

    private Date dateOfBirth;

    private Long facebookAccountId;

    private Long googleAccountId;

    private Role role;
}
