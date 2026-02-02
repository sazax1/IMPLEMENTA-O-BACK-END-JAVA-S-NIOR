package com.seplag.musicapi.service;

import com.seplag.musicapi.dto.request.ArtistRequest;
import com.seplag.musicapi.dto.response.AlbumSimpleResponse;
import com.seplag.musicapi.dto.response.ArtistResponse;
import com.seplag.musicapi.entity.Artist;
import com.seplag.musicapi.entity.enums.ArtistType;
import com.seplag.musicapi.exception.ResourceNotFoundException;
import com.seplag.musicapi.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Transactional(readOnly = true)
    public Page<ArtistResponse> findAll(String name, ArtistType type, Pageable pageable) {
        Page<Artist> artists;

        if (name != null && type != null) {
            artists = artistRepository.findByNameAndType(name, type, pageable);
        } else if (name != null) {
            artists = artistRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (type != null) {
            artists = artistRepository.findByType(type, pageable);
        } else {
            artists = artistRepository.findAll(pageable);
        }

        return artists.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ArtistResponse findById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado com id: " + id));
        return toResponse(artist);
    }

    @Transactional
    public ArtistResponse create(ArtistRequest request) {
        Artist artist = Artist.builder()
                .name(request.getName())
                .type(request.getType())
                .build();

        artist = artistRepository.save(artist);
        return toResponse(artist);
    }

    @Transactional
    public ArtistResponse update(Long id, ArtistRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado com id: " + id));

        artist.setName(request.getName());
        artist.setType(request.getType());

        artist = artistRepository.save(artist);
        return toResponse(artist);
    }

    private ArtistResponse toResponse(Artist artist) {
        return ArtistResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .type(artist.getType())
                .albums(artist.getAlbums().stream()
                        .map(album -> AlbumSimpleResponse.builder()
                                .id(album.getId())
                                .title(album.getTitle())
                                .releaseYear(album.getReleaseYear())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(artist.getCreatedAt())
                .updatedAt(artist.getUpdatedAt())
                .build();
    }
}
