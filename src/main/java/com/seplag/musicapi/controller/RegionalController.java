package com.seplag.musicapi.controller;

import com.seplag.musicapi.dto.response.RegionalResponse;
import com.seplag.musicapi.dto.response.SyncResultResponse;
import com.seplag.musicapi.service.RegionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/regionais")
@RequiredArgsConstructor
@Tag(name = "Regionais", description = "Endpoints para gerenciamento de regionais")
@SecurityRequirement(name = "bearerAuth")
public class RegionalController {

    private final RegionalService regionalService;

    @GetMapping
    @Operation(summary = "Listar regionais", description = "Retorna lista paginada de regionais")
    public ResponseEntity<Page<RegionalResponse>> findAll(
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(regionalService.findAll(ativo, pageable));
    }

    @PostMapping("/sync")
    @Operation(summary = "Sincronizar regionais", description = "Sincroniza regionais com a API externa. Insere novos, inativa ausentes, recria alterados.")
    public ResponseEntity<SyncResultResponse> sync() {
        return ResponseEntity.ok(regionalService.syncFromExternalApi());
    }
}
