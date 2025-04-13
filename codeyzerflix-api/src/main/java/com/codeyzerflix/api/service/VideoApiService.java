package com.codeyzerflix.api.service;

import com.codeyzerflix.common.dto.VideoDTO;
import com.codeyzerflix.common.mapper.VideoCommonMapper;
import com.codeyzerflix.common.model.Video;
import com.codeyzerflix.common.repository.VideoRepository;
import com.codeyzerflix.common.service.GridFsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class VideoApiService {

    private final VideoRepository videoRepository;
    private final GridFsService gridFsService;
    private final VideoCommonMapper videoCommonMapper;

    public VideoDTO getVideo(String id) {
        return videoCommonMapper.toDTO(videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı")));
    }
    
    public InputStream getVideoStream(String id) throws IOException {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı"));
        return gridFsService.getFile(video.getFileId());
    }
    
    public InputStream getVideoStreamRange(String id, long start, long end) throws IOException {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı"));
        return gridFsService.getFileRange(video.getFileId(), start, end);
    }
    
    public long getVideoLength(String id) throws IOException {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı"));
        return gridFsService.getFileLength(video.getFileId());
    }
} 