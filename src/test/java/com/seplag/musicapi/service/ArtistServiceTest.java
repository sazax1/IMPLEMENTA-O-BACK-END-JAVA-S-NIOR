package com.seplag.musicapi.service;

import com.seplag.musicapi.dto.request.ArtistRequest;
import com.seplag.musicapi.dto.response.ArtistResponse;
import com.seplag.musicapi.entity.Artist;
import com.seplag.musicapi.entity.enums.ArtistType;
import com.seplag.musicapi.exception.ResourceNotFoundException;
import com.seplag.musicapi.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    private Artist testArtist;
    private ArtistRequest testRequest;

    @BeforeEach
    void setUp() {
        testArtist = Artist.builder()
                .id(1L)
                .name("Test Artist")
                .type(ArtistType.SOLO)
                .albums(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testRequest = ArtistRequest.builder()
                .name("Test Artist")
                .type(ArtistType.SOLO)
                .build();
    }

    @Test
    @DisplayName("Deve buscar todos os artistas paginados")
    void shouldFindAllArtistsPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> artistPage = new PageImpl<>(Collections.singletonList(testArtist));

        when(artistRepository.findAll(pageable)).thenReturn(artistPage);

        Page<ArtistResponse> result = artistService.findAll(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Artist", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Deve buscar artistas por nome")
    void shouldFindArtistsByName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> artistPage = new PageImpl<>(Collections.singletonList(testArtist));

        when(artistRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(artistPage);

        Page<ArtistResponse> result = artistService.findAll("Test", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar artistas por tipo")
    void shouldFindArtistsByType() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> artistPage = new PageImpl<>(Collections.singletonList(testArtist));

        when(artistRepository.findByType(ArtistType.SOLO, pageable)).thenReturn(artistPage);

        Page<ArtistResponse> result = artistService.findAll(null, ArtistType.SOLO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar artista por ID")
    void shouldFindArtistById() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));

        ArtistResponse result = artistService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Artist", result.getName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando artista não encontrado")
    void shouldThrowExceptionWhenArtistNotFound() {
        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistService.findById(999L));
    }

    @Test
    @DisplayName("Deve criar novo artista")
    void shouldCreateArtist() {
        when(artistRepository.save(any(Artist.class))).thenReturn(testArtist);

        ArtistResponse result = artistService.create(testRequest);

        assertNotNull(result);
        assertEquals("Test Artist", result.getName());
        assertEquals(ArtistType.SOLO, result.getType());
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    @DisplayName("Deve atualizar artista existente")
    void shouldUpdateArtist() {
        ArtistRequest updateRequest = ArtistRequest.builder()
                .name("Updated Artist")
                .type(ArtistType.BAND)
                .build();

        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(artistRepository.save(any(Artist.class))).thenAnswer(i -> i.getArgument(0));

        ArtistResponse result = artistService.update(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Artist", result.getName());
        assertEquals(ArtistType.BAND, result.getType());
    }
}
