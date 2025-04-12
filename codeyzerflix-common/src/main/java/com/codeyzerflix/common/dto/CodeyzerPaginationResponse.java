package com.codeyzerflix.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class CodeyzerPaginationResponse<T> {

    private Long totalRecord;
    private Integer totalPages;
    private List<T> data;
}
