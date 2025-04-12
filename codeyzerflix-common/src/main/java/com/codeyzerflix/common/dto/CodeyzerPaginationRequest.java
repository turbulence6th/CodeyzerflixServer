package com.codeyzerflix.common.dto;

import lombok.Data;

@Data
public class CodeyzerPaginationRequest<T> {

    private Integer page;
    private Integer size;
    private T details;
}
