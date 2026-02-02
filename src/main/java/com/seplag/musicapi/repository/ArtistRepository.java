package com.seplag.musicapi.repository;

import com.seplag.musicapi.entity.Artist;
import com.seplag.musicapi.entity.enums.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Artist> findByType(ArtistType type, Pageable pageable);

    @Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) AND a.type = :type")
    Page<Artist> findByNameAndType(@Param("name") String name, @Param("type") ArtistType type, Pageable pageable);

    List<Artist> findByIdIn(List<Long> ids);

    boolean existsByNameIgnoreCase(String name);
}
