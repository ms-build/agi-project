package com.agi.multimodal.common.repository;

import com.agi.multimodal.common.entity.MediaTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaTagRepository extends JpaRepository<MediaTag, Long> {
    Optional<MediaTag> findByName(String name);
    
    List<MediaTag> findByNameContaining(String keyword);
    
    boolean existsByName(String name);
    
    @Query("SELECT mt FROM MediaTag mt WHERE mt.mediaType = :mediaType")
    List<MediaTag> findByMediaType(String mediaType);
}
