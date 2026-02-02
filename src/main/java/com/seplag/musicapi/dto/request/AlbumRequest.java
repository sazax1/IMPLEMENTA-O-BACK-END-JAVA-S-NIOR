package com.seplag.musicapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumRequest {

    @NotBlank(message = "Título do álbum é obrigatório")
    @Size(max = 300, message = "Título deve ter no máximo 300 caracteres")
    private String title;

    private Integer releaseYear;

    private List<Long> artistIds;
}
