package com.llu1ts.shopapp.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApiPageResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 12412311L;

    private transient List<T> content = new ArrayList<>();

    private int number;

    private int size;

    private int numberOfElements;

    private boolean isFirst;

    private boolean isLast;

    private int totalPages;

    private long totalElements;
}
