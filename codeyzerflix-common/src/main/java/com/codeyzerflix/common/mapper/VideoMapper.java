package com.codeyzerflix.common.mapper;

import com.codeyzerflix.common.dto.VideoDTO;
import com.codeyzerflix.common.model.Video;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoMapper {

    VideoDTO toDTO(Video video);
    List<VideoDTO> toDTOList(List<Video> videoList);
} 