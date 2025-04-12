package com.codeyzerflix.admin.dto;

import lombok.Data;

@Data
public class VideoSaveRequest {
    private String title;
    private String videoType;
    private String fileId;
} 