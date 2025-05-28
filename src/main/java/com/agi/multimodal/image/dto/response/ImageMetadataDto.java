package com.agi.multimodal.image.dto.response;

import com.agi.multimodal.image.entity.ImageMetadata;
import com.agi.multimodal.image.enums.ImageFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 이미지 메타데이터 응답 DTO
 */
@Getter
@Builder
public class ImageMetadataDto {
    private String id;
    private Long userId;
    private String filename;
    private String url;
    private ImageFormat format;
    private Long size;
    private Integer width;
    private Integer height;
    private String description;
    private LocalDateTime uploadedAt;
    private List<String> tags;

    /**
     * ImageMetadata 엔티티로부터 ImageMetadataDto 객체 생성
     * 
     * @param metadata ImageMetadata 엔티티
     * @return ImageMetadataDto 객체
     */
    public static ImageMetadataDto fromEntity(ImageMetadata metadata) {
        return ImageMetadataDto.builder()
                .id(metadata.getId())
                .userId(metadata.getUser().getId())
                .filename(metadata.getFilename())
                .url(metadata.getUrl())
                .format(metadata.getFormat())
                .size(metadata.getSize())
                .width(metadata.getWidth())
                .height(metadata.getHeight())
                .description(metadata.getDescription())
                .uploadedAt(metadata.getUploadedAt())
                .tags(metadata.getTags() != null ? 
                        metadata.getTags().stream()
                                .map(tag -> tag.getName())
                                .collect(Collectors.toList()) : 
                        null)
                .build();
    }
}
