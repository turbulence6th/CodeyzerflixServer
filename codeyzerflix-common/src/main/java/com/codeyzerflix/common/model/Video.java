package com.codeyzerflix.common.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Data
@Document(collection = "videos")
public class Video {
    @Id
    private String id;
    
    private String title;
    
    @Field("file_id")
    private ObjectId fileId;
    
    @Field("file_name")
    private String fileName;

    @Field("content_type")
    private String contentType;

    @Field(value = "video_type", targetType = FieldType.STRING)
    private VideoType videoType;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
} 