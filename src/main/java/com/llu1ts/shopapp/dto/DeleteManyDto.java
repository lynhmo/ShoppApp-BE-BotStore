package com.llu1ts.shopapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteManyDto {
    private List<Long> ids;
}
