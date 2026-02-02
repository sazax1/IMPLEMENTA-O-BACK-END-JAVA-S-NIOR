package com.seplag.musicapi.repository;

import com.seplag.musicapi.entity.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    Optional<Regional> findByExternalIdAndAtivoTrue(Integer externalId);

    List<Regional> findByAtivoTrue();

    List<Regional> findByExternalIdIn(List<Integer> externalIds);

    @Modifying
    @Query("UPDATE Regional r SET r.ativo = false WHERE r.externalId = :externalId AND r.ativo = true")
    void inactivateByExternalId(@Param("externalId") Integer externalId);

    @Modifying
    @Query("UPDATE Regional r SET r.ativo = false WHERE r.externalId NOT IN :externalIds AND r.ativo = true")
    void inactivateNotInExternalIds(@Param("externalIds") List<Integer> externalIds);

    boolean existsByExternalIdAndNomeAndAtivoTrue(Integer externalId, String nome);
}
