package com.seplag.musicapi.service;

import com.seplag.musicapi.dto.external.RegionalExternalDto;
import com.seplag.musicapi.dto.response.RegionalResponse;
import com.seplag.musicapi.dto.response.SyncResultResponse;
import com.seplag.musicapi.entity.Regional;
import com.seplag.musicapi.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalService {

    private static final String EXTERNAL_API_URL = "https://integrador-argus-api.geia.vip/v1/regionais";

    private final RegionalRepository regionalRepository;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public Page<RegionalResponse> findAll(Boolean ativo, Pageable pageable) {
        Page<Regional> regionais;

        if (ativo != null && ativo) {
            regionais = regionalRepository.findAll(pageable);
        } else {
            regionais = regionalRepository.findAll(pageable);
        }

        return regionais.map(this::toResponse);
    }

    @Transactional
    public SyncResultResponse syncFromExternalApi() {
        log.info("Iniciando sincronização de regionais...");

        // 1. Buscar dados da API externa
        List<RegionalExternalDto> externalData = fetchExternalData();

        if (externalData == null || externalData.isEmpty()) {
            return SyncResultResponse.builder()
                    .inserted(0)
                    .inactivated(0)
                    .updated(0)
                    .message("Nenhum dado retornado pela API externa")
                    .build();
        }

        int inserted = 0;
        int inactivated = 0;
        int updated = 0;

        // Mapear dados externos por ID
        Map<Integer, RegionalExternalDto> externalMap = externalData.stream()
                .collect(Collectors.toMap(RegionalExternalDto::getId, dto -> dto));

        Set<Integer> externalIds = externalMap.keySet();

        // 2. Buscar regionais ativos existentes
        List<Regional> existingActive = regionalRepository.findByAtivoTrue();

        for (Regional existing : existingActive) {
            Integer extId = existing.getExternalId();

            if (!externalIds.contains(extId)) {
                // Caso 2: Ausente no endpoint → inativar
                existing.setAtivo(false);
                regionalRepository.save(existing);
                inactivated++;
                log.info("Regional inativado (ausente na API): {} - {}", extId, existing.getNome());
            } else {
                RegionalExternalDto extDto = externalMap.get(extId);

                if (!existing.getNome().equals(extDto.getNome())) {
                    // Caso 3: Atributo alterado → inativar antigo e criar novo
                    existing.setAtivo(false);
                    regionalRepository.save(existing);
                    inactivated++;

                    Regional newRegional = Regional.builder()
                            .externalId(extDto.getId())
                            .nome(extDto.getNome())
                            .ativo(true)
                            .build();
                    regionalRepository.save(newRegional);
                    updated++;
                    log.info("Regional atualizado (nome alterado): {} - {} -> {}", extId, existing.getNome(),
                            extDto.getNome());
                }
                // Remover do map para identificar novos
                externalMap.remove(extId);
            }
        }

        // Remover os que já existem ativos (e não foram alterados) do map
        for (Regional existing : existingActive) {
            externalMap.remove(existing.getExternalId());
        }

        // 3. Caso 1: Novos no endpoint → inserir
        for (RegionalExternalDto dto : externalMap.values()) {
            // Verificar se já existe (mesmo que inativo)
            if (!regionalRepository.existsByExternalIdAndNomeAndAtivoTrue(dto.getId(), dto.getNome())) {
                Regional newRegional = Regional.builder()
                        .externalId(dto.getId())
                        .nome(dto.getNome())
                        .ativo(true)
                        .build();
                regionalRepository.save(newRegional);
                inserted++;
                log.info("Regional inserido: {} - {}", dto.getId(), dto.getNome());
            }
        }

        String message = String.format("Sincronização concluída: %d inseridos, %d inativados, %d atualizados",
                inserted, inactivated, updated);
        log.info(message);

        return SyncResultResponse.builder()
                .inserted(inserted)
                .inactivated(inactivated)
                .updated(updated)
                .message(message)
                .build();
    }

    private List<RegionalExternalDto> fetchExternalData() {
        try {
            ResponseEntity<List<RegionalExternalDto>> response = restTemplate.exchange(
                    EXTERNAL_API_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            log.error("Erro ao buscar dados da API externa: {}", e.getMessage());
            throw new RuntimeException("Falha ao conectar com a API externa de regionais", e);
        }
    }

    private RegionalResponse toResponse(Regional regional) {
        return RegionalResponse.builder()
                .id(regional.getId())
                .externalId(regional.getExternalId())
                .nome(regional.getNome())
                .ativo(regional.getAtivo())
                .createdAt(regional.getCreatedAt())
                .updatedAt(regional.getUpdatedAt())
                .build();
    }
}
