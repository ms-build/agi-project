package com.agi.multimodal.video.dto.response;

import com.agi.multimodal.video.enums.VideoFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 비디오 메타데이터 응답 DTO
 */
@Getter
@Builder
public class VideoMetadataDto {
    private String id;
    private Long userId;
    private String filename;
    private String url;
    private VideoFormat format;
    private Long size;
    private Integer duration;
    private Integer width;
    private Integer height;
    private String description;
    private LocalDateTime uploadedAt;
    private List<String> tags;
}
