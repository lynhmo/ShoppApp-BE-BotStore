package com.llu1ts.shopapp.response;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BaseResponse {
    protected LocalDateTime createAt;
    protected LocalDateTime updateAt;
}
