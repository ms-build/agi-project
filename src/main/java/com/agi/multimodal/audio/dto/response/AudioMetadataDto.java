package com.agi.multimodal.audio.dto.response;

import com.agi.multimodal.audio.enums.AudioFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 오디오 메타데이터 응답 DTO
 */
@Getter
@Builder
public class AudioMetadataDto {
    private String id;
    private Long userId;
    private String filename;
    private String url;
    private AudioFormat format;
    private Long size;
    private Integer duration;
    private String description;
    private LocalDateTime uploadedAt;
    private List<String> tags;
}
