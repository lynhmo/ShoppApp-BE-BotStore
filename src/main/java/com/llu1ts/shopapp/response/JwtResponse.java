package com.llu1ts.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class JwtResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("time_unit")
    private final String timeUnit = "milliseconds";
}
