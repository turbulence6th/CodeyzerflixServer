package com.codeyzerflix.admin.controller;

import com.codeyzerflix.admin.dto.VideoSaveRequest;
import com.codeyzerflix.admin.dto.VideoUpdateRequest;
import com.codeyzerflix.admin.service.VideoAdminService;
import com.codeyzerflix.common.dto.CodeyzerPaginationRequest;
import com.codeyzerflix.common.dto.CodeyzerPaginationResponse;
import com.codeyzerflix.common.dto.VideoDTO;
import com.codeyzerflix.common.dto.VideoFilterDTO;
import com.codeyzerflix.common.service.VideoCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoAdminController {

    private final VideoCommonService videoCommonService;
    private final VideoAdminService videoAdminService;

    @PostMapping
    public ResponseEntity<CodeyzerPaginationResponse<VideoDTO>> getAllVideos(@RequestBody CodeyzerPaginationRequest<VideoFilterDTO> request) {
        return ResponseEntity.ok(videoCommonService.getAllVideos(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideo(@PathVariable String id) {
        VideoDTO video = videoAdminService.getVideo(id);
        return ResponseEntity.ok(video);
    }

    @PostMapping("/save")
    public ResponseEntity<VideoDTO> saveVideo(@RequestBody VideoSaveRequest request) throws IOException {
        VideoDTO video = videoAdminService.saveVideo(request);
        return ResponseEntity.ok(video);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable String id, @RequestBody VideoUpdateRequest request) throws IOException {
        VideoDTO video = videoAdminService.updateVideo(id, request);
        return ResponseEntity.ok(video);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable String id) {
        videoAdminService.deleteVideo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
        String fileId = videoAdminService.upload(file);
        return ResponseEntity.ok(fileId);
    }
} 