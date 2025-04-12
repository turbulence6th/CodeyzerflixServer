package com.codeyzerflix.common.dto;

import lombok.Data;

@Data
public class VideoDTO {
    private String id;
    private String title;
    private String fileName;
    private String contentType;
    private String videoType;
} 