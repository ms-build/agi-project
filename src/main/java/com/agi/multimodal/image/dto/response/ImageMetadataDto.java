package com.agi.multimodal.image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageMetadataDto {
    private String id;
    private Long userId;
    private String name;
    private String description;
    private String url;
    private String contentType;
    private Long size;
    private Integer width;
    private Integer height;
    private String[] tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
