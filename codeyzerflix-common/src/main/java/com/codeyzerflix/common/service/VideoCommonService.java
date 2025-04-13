package com.codeyzerflix.common.service;

import com.codeyzerflix.common.dto.CodeyzerPaginationRequest;
import com.codeyzerflix.common.dto.CodeyzerPaginationResponse;
import com.codeyzerflix.common.dto.VideoDTO;
import com.codeyzerflix.common.dto.VideoFilterDTO;
import com.codeyzerflix.common.mapper.VideoCommonMapper;
import com.codeyzerflix.common.model.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VideoCommonService {

    private final VideoCommonMapper videoCommonMapper;
    private final MongoTemplate mongoTemplate;

    public CodeyzerPaginationResponse<VideoDTO> getAllVideos(CodeyzerPaginationRequest<VideoFilterDTO> request) {
        Page<Video> page = getVideosPage(request);

        CodeyzerPaginationResponse<VideoDTO> response = new CodeyzerPaginationResponse<>();
        response.setTotalRecord(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setData(videoCommonMapper.toDTOList(page.getContent()));

        return response;
    }

    private Page<Video> getVideosPage(CodeyzerPaginationRequest<VideoFilterDTO> request) {
        Query query = new Query();

        VideoFilterDTO videoFilterDTO = request.getDetails();
        if (videoFilterDTO != null) {
            if (videoFilterDTO.getKeyword() != null && !videoFilterDTO.getKeyword().trim().isEmpty()) {
                TextCriteria textCriteria = TextCriteria.forLanguage("turkish").matching(videoFilterDTO.getKeyword());
                query.addCriteria(textCriteria);

                query = TextQuery.queryText(textCriteria)
                        .sortByScore();
            }

            if (videoFilterDTO.getVideoType() != null && !videoFilterDTO.getVideoType().trim().isEmpty()) {
                query.addCriteria(Criteria.where("videoType").is(videoFilterDTO.getVideoType()));
            }
        }

        long total = mongoTemplate.count(query, Video.class);

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        query.with(pageable);

        List<Video> videos = mongoTemplate.find(query, Video.class);

        return new PageImpl<>(videos, pageable, total);
    }
}