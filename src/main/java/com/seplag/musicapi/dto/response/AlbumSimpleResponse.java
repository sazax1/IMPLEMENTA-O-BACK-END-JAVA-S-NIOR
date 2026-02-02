package com.seplag.musicapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumSimpleResponse {

    private Long id;
    private String title;
    private Integer releaseYear;
}
