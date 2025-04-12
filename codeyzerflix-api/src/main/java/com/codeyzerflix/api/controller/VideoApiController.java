package com.codeyzerflix.api.controller;

import com.codeyzerflix.api.service.VideoApiService;
import com.codeyzerflix.common.dto.CodeyzerPaginationRequest;
import com.codeyzerflix.common.dto.CodeyzerPaginationResponse;
import com.codeyzerflix.common.dto.VideoDTO;
import com.codeyzerflix.common.dto.VideoFilterDTO;
import com.codeyzerflix.common.service.VideoCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoApiController {

    private final VideoCommonService videoCommonService;
    private final VideoApiService videoApiService;

    @PostMapping
    public ResponseEntity<CodeyzerPaginationResponse<VideoDTO>> getAllVideos(@RequestBody CodeyzerPaginationRequest<VideoFilterDTO> request) {
        CodeyzerPaginationResponse<VideoDTO> response = videoCommonService.getAllVideos(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideo(@PathVariable String id) {
        VideoDTO video = videoApiService.getVideo(id);
        return ResponseEntity.ok(video);
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<InputStreamResource> streamVideo(
            @PathVariable String id,
            @RequestHeader HttpHeaders headers) throws IOException {
        
        VideoDTO video = videoApiService.getVideo(id);
        long fileLength = videoApiService.getVideoLength(id);
        
        // Range header'ı kontrol et
        List<HttpRange> ranges = headers.getRange();
        if (!ranges.isEmpty()) {
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(fileLength);
            long end = range.getRangeEnd(fileLength);
            
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength))
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(end - start + 1))
                    .contentType(MediaType.parseMediaType(video.getContentType()))
                    .body(new InputStreamResource(videoApiService.getVideoStreamRange(id, start, end)));
        }
        
        // Range header yoksa tüm dosyayı gönder
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video.getContentType()))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                .body(new InputStreamResource(videoApiService.getVideoStream(id)));
    }
} 