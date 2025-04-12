package com.codeyzerflix.admin.service;

import com.codeyzerflix.admin.config.AdminProperties;
import com.codeyzerflix.admin.dto.VideoSaveRequest;
import com.codeyzerflix.common.dto.VideoDTO;
import com.codeyzerflix.common.mapper.VideoMapper;
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
    private final VideoMapper videoMapper;

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
        }

        String extension = FileTypeDetector.getFileExtension(contentType);
        String fileName = baseFileName + extension;

        // Video dosyasını GridFS'e yükle
        return gridFsService.saveFile(inputStream, fileName, contentType);
    }

    @Transactional(readOnly = true)
    public VideoDTO getVideo(String id) {
        return videoRepository.findById(id)
                .map(videoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Video bulunamadı"));
    }

    @Transactional
    public VideoDTO saveVideo(VideoSaveRequest request) throws IOException {

        GridFSFile gridFSFile = gridFsService.getGridFSFile(request.getFileId());

        // Video nesnesini oluştur ve kaydet
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setVideoType(VideoType.valueOf(request.getVideoType()));
        video.setFileId(request.getFileId());
        video.setFileName(gridFSFile.getFilename());
        video.setContentType(gridFSFile.getMetadata().getString("_contentType"));

        Video saved = videoRepository.save(video);
        return videoMapper.toDTO(saved);
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