package com.seplag.musicapi.dto.response;

import com.seplag.musicapi.entity.enums.ArtistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistResponse {

    private Long id;
    private String name;
    private ArtistType type;
    private List<AlbumSimpleResponse> albums;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
