package com.seplag.musicapi.repository;

import com.seplag.musicapi.entity.Album;
import com.seplag.musicapi.entity.enums.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Page<Album> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT al FROM Album al JOIN al.artists ar WHERE ar.type = :artistType")
    Page<Album> findByArtistType(@Param("artistType") ArtistType artistType, Pageable pageable);

    @Query("SELECT DISTINCT al FROM Album al JOIN al.artists ar WHERE ar.id = :artistId")
    Page<Album> findByArtistId(@Param("artistId") Long artistId, Pageable pageable);

    @Query("SELECT DISTINCT al FROM Album al JOIN al.artists ar WHERE LOWER(ar.name) LIKE LOWER(CONCAT('%', :artistName, '%'))")
    Page<Album> findByArtistNameContaining(@Param("artistName") String artistName, Pageable pageable);

    boolean existsByTitleIgnoreCase(String title);
}
