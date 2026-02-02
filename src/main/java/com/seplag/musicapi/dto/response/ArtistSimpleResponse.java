package com.seplag.musicapi.dto.response;

import com.seplag.musicapi.entity.enums.ArtistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSimpleResponse {

    private Long id;
    private String name;
    private ArtistType type;
}
