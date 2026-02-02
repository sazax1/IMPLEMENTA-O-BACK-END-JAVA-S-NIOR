package com.seplag.musicapi.service;

import com.seplag.musicapi.dto.request.AlbumRequest;
import com.seplag.musicapi.dto.response.AlbumCoverResponse;
import com.seplag.musicapi.dto.response.AlbumResponse;
import com.seplag.musicapi.dto.response.ArtistSimpleResponse;
import com.seplag.musicapi.entity.Album;
import com.seplag.musicapi.entity.AlbumCover;
import com.seplag.musicapi.entity.Artist;
import com.seplag.musicapi.entity.enums.ArtistType;
import com.seplag.musicapi.exception.ResourceNotFoundException;
import com.seplag.musicapi.repository.AlbumCoverRepository;
import com.seplag.musicapi.repository.AlbumRepository;
import com.seplag.musicapi.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumCoverRepository albumCoverRepository;
    private final MinioService minioService;
    private final WebSocketService webSocketService;

    @Transactional(readOnly = true)
    public Page<AlbumResponse> findAll(String title, String artistName, ArtistType artistType, Pageable pageable) {
        Page<Album> albums;

        if (artistType != null) {
            albums = albumRepository.findByArtistType(artistType, pageable);
        } else if (artistName != null) {
            albums = albumRepository.findByArtistNameContaining(artistName, pageable);
        } else if (title != null) {
            albums = albumRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            albums = albumRepository.findAll(pageable);
        }

        return albums.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AlbumResponse findById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com id: " + id));
        return toResponse(album);
    }

    @Transactional
    public AlbumResponse create(AlbumRequest request) {
        Album album = Album.builder()
                .title(request.getTitle())
                .releaseYear(request.getReleaseYear())
                .artists(new HashSet<>())
                .covers(new ArrayList<>())
                .build();

        if (request.getArtistIds() != null && !request.getArtistIds().isEmpty()) {
            List<Artist> artists = artistRepository.findByIdIn(request.getArtistIds());
            for (Artist artist : artists) {
                artist.addAlbum(album);
            }
        }

        album = albumRepository.save(album);

        AlbumResponse response = toResponse(album);
        webSocketService.notifyNewAlbum(response);

        return response;
    }

    @Transactional
    public AlbumResponse update(Long id, AlbumRequest request) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com id: " + id));

        album.setTitle(request.getTitle());
        album.setReleaseYear(request.getReleaseYear());

        // Atualizar artistas
        if (request.getArtistIds() != null) {
            // Remover associações antigas
            for (Artist artist : new HashSet<>(album.getArtists())) {
                artist.removeAlbum(album);
            }

            // Adicionar novas associações
            List<Artist> newArtists = artistRepository.findByIdIn(request.getArtistIds());
            for (Artist artist : newArtists) {
                artist.addAlbum(album);
            }
        }

        album = albumRepository.save(album);
        return toResponse(album);
    }

    @Transactional
    public List<AlbumCoverResponse> uploadCovers(Long albumId, List<MultipartFile> files) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com id: " + albumId));

        List<AlbumCoverResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileKey = minioService.uploadFile(file, albumId);

            AlbumCover cover = AlbumCover.builder()
                    .album(album)
                    .fileKey(fileKey)
                    .originalName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            cover = albumCoverRepository.save(cover);

            responses.add(AlbumCoverResponse.builder()
                    .id(cover.getId())
                    .originalName(cover.getOriginalName())
                    .contentType(cover.getContentType())
                    .fileSize(cover.getFileSize())
                    .presignedUrl(minioService.getPresignedUrl(fileKey))
                    .createdAt(cover.getCreatedAt())
                    .build());
        }

        return responses;
    }

    private AlbumResponse toResponse(Album album) {
        List<AlbumCoverResponse> coverResponses = album.getCovers().stream()
                .map(cover -> AlbumCoverResponse.builder()
                        .id(cover.getId())
                        .originalName(cover.getOriginalName())
                        .contentType(cover.getContentType())
                        .fileSize(cover.getFileSize())
                        .presignedUrl(minioService.getPresignedUrl(cover.getFileKey()))
                        .createdAt(cover.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .releaseYear(album.getReleaseYear())
                .artists(album.getArtists().stream()
                        .map(artist -> ArtistSimpleResponse.builder()
                                .id(artist.getId())
                                .name(artist.getName())
                                .type(artist.getType())
                                .build())
                        .collect(Collectors.toList()))
                .covers(coverResponses)
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }
}
