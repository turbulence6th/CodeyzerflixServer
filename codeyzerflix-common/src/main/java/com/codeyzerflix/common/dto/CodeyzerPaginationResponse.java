package com.codeyzerflix.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class CodeyzerPaginationResponse<T> {

    private Integer totalRecord;
    private Integer totalPages;
    private List<T> data;
}
