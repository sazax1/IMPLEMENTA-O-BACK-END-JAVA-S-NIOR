package com.seplag.musicapi.repository;

import com.seplag.musicapi.entity.AlbumCover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumCoverRepository extends JpaRepository<AlbumCover, Long> {

    List<AlbumCover> findByAlbumId(Long albumId);

    void deleteByAlbumId(Long albumId);
}
