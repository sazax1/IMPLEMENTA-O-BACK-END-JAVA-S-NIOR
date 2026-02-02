package com.seplag.musicapi.service;

import com.seplag.musicapi.dto.external.RegionalExternalDto;
import com.seplag.musicapi.dto.response.SyncResultResponse;
import com.seplag.musicapi.entity.Regional;
import com.seplag.musicapi.repository.RegionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalServiceTest {

    @Mock
    private RegionalRepository regionalRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RegionalService regionalService;

    private Regional existingRegional;
    private RegionalExternalDto externalDto;

    @BeforeEach
    void setUp() {
        existingRegional = Regional.builder()
                .id(1L)
                .externalId(9)
                .nome("REGIONAL DE CUIABÁ")
                .ativo(true)
                .build();

        externalDto = RegionalExternalDto.builder()
                .id(9)
                .nome("REGIONAL DE CUIABÁ")
                .build();
    }

    @Test
    @DisplayName("Deve inserir novos regionais")
    void shouldInsertNewRegionais() {
        RegionalExternalDto newDto = RegionalExternalDto.builder()
                .id(100)
                .nome("NOVA REGIONAL")
                .build();

        List<RegionalExternalDto> externalList = Collections.singletonList(newDto);
        ResponseEntity<List<RegionalExternalDto>> responseEntity = new ResponseEntity<>(externalList, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        when(regionalRepository.findByAtivoTrue()).thenReturn(Collections.emptyList());
        when(regionalRepository.existsByExternalIdAndNomeAndAtivoTrue(100, "NOVA REGIONAL")).thenReturn(false);
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> i.getArgument(0));

        SyncResultResponse result = regionalService.syncFromExternalApi();

        assertNotNull(result);
        assertEquals(1, result.getInserted());
        assertEquals(0, result.getInactivated());
    }

    @Test
    @DisplayName("Deve inativar regionais ausentes na API externa")
    void shouldInactivateAbsentRegionais() {
        List<RegionalExternalDto> externalList = Collections.emptyList();
        ResponseEntity<List<RegionalExternalDto>> responseEntity = new ResponseEntity<>(externalList, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        when(regionalRepository.findByAtivoTrue()).thenReturn(Collections.singletonList(existingRegional));
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> i.getArgument(0));

        SyncResultResponse result = regionalService.syncFromExternalApi();

        assertNotNull(result);
        assertEquals(1, result.getInactivated());
        assertFalse(existingRegional.getAtivo());
    }

    @Test
    @DisplayName("Deve atualizar regional quando nome alterado")
    void shouldUpdateRegionalWhenNameChanged() {
        RegionalExternalDto changedDto = RegionalExternalDto.builder()
                .id(9)
                .nome("REGIONAL DE CUIABÁ - ATUALIZADO")
                .build();

        List<RegionalExternalDto> externalList = Collections.singletonList(changedDto);
        ResponseEntity<List<RegionalExternalDto>> responseEntity = new ResponseEntity<>(externalList, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        when(regionalRepository.findByAtivoTrue()).thenReturn(Collections.singletonList(existingRegional));
        when(regionalRepository.save(any(Regional.class))).thenAnswer(i -> i.getArgument(0));

        SyncResultResponse result = regionalService.syncFromExternalApi();

        assertNotNull(result);
        assertEquals(1, result.getInactivated());
        assertEquals(1, result.getUpdated());
    }

    @Test
    @DisplayName("Deve manter regional quando não há alteração")
    void shouldKeepRegionalWhenNoChange() {
        List<RegionalExternalDto> externalList = Collections.singletonList(externalDto);
        ResponseEntity<List<RegionalExternalDto>> responseEntity = new ResponseEntity<>(externalList, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        when(regionalRepository.findByAtivoTrue()).thenReturn(Collections.singletonList(existingRegional));

        SyncResultResponse result = regionalService.syncFromExternalApi();

        assertNotNull(result);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getInactivated());
        assertEquals(0, result.getUpdated());
    }
}
