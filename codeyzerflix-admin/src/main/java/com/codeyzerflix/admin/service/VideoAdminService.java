package com.codeyzerflix.admin.service;

import com.codeyzerflix.admin.config.AdminProperties;
import com.codeyzerflix.admin.dto.VideoSaveRequest;
import com.codeyzerflix.admin.dto.VideoUpdateRequest;
import com.codeyzerflix.admin.mapper.VideoAdminMapper;
import com.codeyzerflix.common.dto.VideoDTO;
import com.codeyzerflix.common.mapper.VideoCommonMapper;
import com.codeyzerflix.common.model.Video;
import com.codeyzerflix.common.model.VideoType;
import com.codeyzerflix.common.repository.VideoRepository;
import com.codeyzerflix.common.service.GridFsService;
import com.codeyzerflix.common.util.FileTypeDetector;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class VideoAdminService {

    private final VideoRepository videoRepository;
    private final GridFsService gridFsService;
    private final AdminProperties adminProperties;
    private final Flv2Mp4Converter flv2Mp4Converter;
    private final Divx2Mp4Converter divx2Mp4Converter;
    private final VideoCommonMapper videoCommonMapper;
    private final VideoAdminMapper videoAdminMapper;

    @Transactional
    public String upload(MultipartFile file) throws IOException, InterruptedException {
        // Video dosyasının tipini kontrol et
        String contentType = FileTypeDetector.detectVideoType(file);
        if (!adminProperties.getAllowedVideoTypes().contains(contentType)) {
            throw new IllegalArgumentException("Desteklenmeyen video formatı");
        }

        // Dosya adını MIME type'a göre güncelle
        String originalFileName = file.getOriginalFilename();
        String baseFileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));

        InputStream inputStream = file.getInputStream();
        if (contentType.equals("video/x-flv")) {
            contentType = "video/mp4";
            inputStream = flv2Mp4Converter.convertFlvToMp4(inputStream);
        } else if (contentType.equals("video/x-divx")) {
            contentType = "video/mp4";
            inputStream = divx2Mp4Converter.convertDivxToMp4(inputStream);
        }

        String extension = FileTypeDetector.getFileExtension(contentType);
        String fileName = baseFileName + extension;

        // Video dosyasını GridFS'e yükle
        return gridFsService.saveFile(inputStream, fileName, contentType);
    }

    @Transactional(readOnly = true)
    public VideoDTO getVideo(String id) {
        return videoRepository.findById(id)
                .map(videoCommonMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı"));
    }

    @Transactional
    public VideoDTO saveVideo(VideoSaveRequest request) throws IOException {
        // Video nesnesini oluştur ve kaydet
        Video video = videoAdminMapper.creteEntity(request);

        GridFSFile gridFSFile = gridFsService.getGridFSFile(video.getFileId());

        video.setContentType(gridFSFile.getMetadata().getString("_contentType"));

        Video saved = videoRepository.save(video);
        return videoCommonMapper.toDTO(saved);
    }

    @Transactional
    public VideoDTO updateVideo(String id, VideoUpdateRequest request) throws IOException {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı"));

        videoAdminMapper.updateEntity(video, request);

        Video saved = videoRepository.save(video);
        return videoCommonMapper.toDTO(saved);
    }

    @Transactional
    public void deleteVideo(String id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı"));
        
        // GridFS'den dosyaları sil
        gridFsService.deleteFile(video.getFileId());
        
        // Video nesnesini sil
        videoRepository.deleteById(id);
    }
} 