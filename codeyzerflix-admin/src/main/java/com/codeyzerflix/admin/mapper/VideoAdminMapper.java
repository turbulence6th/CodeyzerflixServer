package com.codeyzerflix.admin.mapper;

import com.codeyzerflix.admin.dto.VideoSaveRequest;
import com.codeyzerflix.admin.dto.VideoUpdateRequest;
import com.codeyzerflix.common.mapper.ObjectIdMapper;
import com.codeyzerflix.common.model.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public interface VideoAdminMapper {

   
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "contentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Video creteEntity(VideoSaveRequest videoSaveRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileId", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "contentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Video video, VideoUpdateRequest videoUpdateRequest);
}
