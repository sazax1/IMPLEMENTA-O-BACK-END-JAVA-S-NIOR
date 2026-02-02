package com.seplag.musicapi.controller;

import com.seplag.musicapi.dto.request.AlbumRequest;
import com.seplag.musicapi.dto.response.AlbumCoverResponse;
import com.seplag.musicapi.dto.response.AlbumResponse;
import com.seplag.musicapi.entity.enums.ArtistType;
import com.seplag.musicapi.service.AlbumService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
@Tag(name = "Álbuns", description = "Endpoints para gerenciamento de álbuns")
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    @Operation(summary = "Listar álbuns", description = "Retorna lista paginada de álbuns com filtros opcionais")
    public ResponseEntity<Page<AlbumResponse>> findAll(
            @Parameter(description = "Filtrar por título do álbum") @RequestParam(required = false) String title,
            @Parameter(description = "Filtrar por nome do artista") @RequestParam(required = false) String artistName,
            @Parameter(description = "Filtrar por tipo do artista (SOLO para cantores, BAND para bandas)") @RequestParam(required = false) ArtistType artistType,
            @PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(albumService.findAll(title, artistName, artistType, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar álbum por ID", description = "Retorna um álbum específico pelo ID")
    public ResponseEntity<AlbumResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar álbum", description = "Cria um novo álbum e notifica via WebSocket")
    public ResponseEntity<AlbumResponse> create(@Valid @RequestBody AlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar álbum", description = "Atualiza os dados de um álbum existente")
    public ResponseEntity<AlbumResponse> update(@PathVariable Long id, @Valid @RequestBody AlbumRequest request) {
        return ResponseEntity.ok(albumService.update(id, request));
    }

    @PostMapping(value = "/{id}/covers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de capas", description = "Faz upload de uma ou mais imagens de capa do álbum")
    public ResponseEntity<List<AlbumCoverResponse>> uploadCovers(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.uploadCovers(id, files));
    }
}
