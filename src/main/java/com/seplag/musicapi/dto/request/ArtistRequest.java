package com.seplag.musicapi.dto.request;

import com.seplag.musicapi.entity.enums.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistRequest {

    @NotBlank(message = "Nome do artista é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    @NotNull(message = "Tipo do artista é obrigatório (SOLO ou BAND)")
    private ArtistType type;
}
