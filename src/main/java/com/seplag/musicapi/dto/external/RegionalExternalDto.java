package com.seplag.musicapi.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalExternalDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("nome")
    private String nome;
}
