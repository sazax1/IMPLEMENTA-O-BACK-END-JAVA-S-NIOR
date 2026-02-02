package com.seplag.musicapi.controller;

import com.seplag.musicapi.dto.request.ArtistRequest;
import com.seplag.musicapi.dto.response.ArtistResponse;
import com.seplag.musicapi.entity.enums.ArtistType;
import com.seplag.musicapi.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
@Tag(name = "Artistas", description = "Endpoints para gerenciamento de artistas")
@SecurityRequirement(name = "bearerAuth")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    @Operation(summary = "Listar artistas", description = "Retorna lista paginada de artistas com filtros opcionais")
    public ResponseEntity<Page<ArtistResponse>> findAll(
            @Parameter(description = "Filtrar por nome do artista") @RequestParam(required = false) String name,
            @Parameter(description = "Filtrar por tipo (SOLO ou BAND)") @RequestParam(required = false) ArtistType type,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(artistService.findAll(name, type, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar artista por ID", description = "Retorna um artista específico pelo ID")
    public ResponseEntity<ArtistResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar artista", description = "Cria um novo artista")
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody ArtistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista", description = "Atualiza os dados de um artista existente")
    public ResponseEntity<ArtistResponse> update(@PathVariable Long id, @Valid @RequestBody ArtistRequest request) {
        return ResponseEntity.ok(artistService.update(id, request));
    }
}
