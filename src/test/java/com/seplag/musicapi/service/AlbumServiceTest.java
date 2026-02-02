package com.seplag.musicapi.service;

import com.seplag.musicapi.dto.request.AlbumRequest;
import com.seplag.musicapi.dto.response.AlbumResponse;
import com.seplag.musicapi.entity.Album;
import com.seplag.musicapi.entity.Artist;
import com.seplag.musicapi.entity.enums.ArtistType;
import com.seplag.musicapi.exception.ResourceNotFoundException;
import com.seplag.musicapi.repository.AlbumCoverRepository;
import com.seplag.musicapi.repository.AlbumRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumCoverRepository albumCoverRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private AlbumService albumService;

    private Album testAlbum;
    private Artist testArtist;
    private AlbumRequest testRequest;

    @BeforeEach
    void setUp() {
        testArtist = Artist.builder()
                .id(1L)
                .name("Test Artist")
                .type(ArtistType.SOLO)
                .albums(new HashSet<>())
                .build();

        testAlbum = Album.builder()
                .id(1L)
                .title("Test Album")
                .releaseYear(2024)
                .artists(new HashSet<>())
                .covers(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testRequest = AlbumRequest.builder()
                .title("Test Album")
                .releaseYear(2024)
                .artistIds(Collections.singletonList(1L))
                .build();
    }

    @Test
    @DisplayName("Deve buscar todos os álbuns paginados")
    void shouldFindAllAlbumsPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> albumPage = new PageImpl<>(Collections.singletonList(testAlbum));

        when(albumRepository.findAll(pageable)).thenReturn(albumPage);

        Page<AlbumResponse> result = albumService.findAll(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Album", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Deve buscar álbuns por tipo de artista")
    void shouldFindAlbumsByArtistType() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> albumPage = new PageImpl<>(Collections.singletonList(testAlbum));

        when(albumRepository.findByArtistType(ArtistType.BAND, pageable)).thenReturn(albumPage);

        Page<AlbumResponse> result = albumService.findAll(null, null, ArtistType.BAND, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar álbum por ID")
    void shouldFindAlbumById() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));

        AlbumResponse result = albumService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Album", result.getTitle());
    }

    @Test
    @DisplayName("Deve lançar exceção quando álbum não encontrado")
    void shouldThrowExceptionWhenAlbumNotFound() {
        when(albumRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> albumService.findById(999L));
    }

    @Test
    @DisplayName("Deve criar novo álbum e notificar via WebSocket")
    void shouldCreateAlbumAndNotify() {
        when(artistRepository.findByIdIn(anyList())).thenReturn(Collections.singletonList(testArtist));
        when(albumRepository.save(any(Album.class))).thenReturn(testAlbum);

        AlbumResponse result = albumService.create(testRequest);

        assertNotNull(result);
        assertEquals("Test Album", result.getTitle());
        verify(webSocketService, times(1)).notifyNewAlbum(any(AlbumResponse.class));
    }

    @Test
    @DisplayName("Deve atualizar álbum existente")
    void shouldUpdateAlbum() {
        AlbumRequest updateRequest = AlbumRequest.builder()
                .title("Updated Album")
                .releaseYear(2025)
                .artistIds(Collections.emptyList())
                .build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(artistRepository.findByIdIn(anyList())).thenReturn(Collections.emptyList());
        when(albumRepository.save(any(Album.class))).thenAnswer(i -> i.getArgument(0));

        AlbumResponse result = albumService.update(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Album", result.getTitle());
        assertEquals(2025, result.getReleaseYear());
    }
}
