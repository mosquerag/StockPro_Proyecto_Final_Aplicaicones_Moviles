package com.stockpro.repository;

import com.stockpro.model.entity.EntradaPrevista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface EntradaPrevistaRepository extends JpaRepository<EntradaPrevista, UUID> {
    List<EntradaPrevista> findByNegocioIdAndFechaPrevistaOrderByCreatedAtDesc(UUID negocioId, LocalDate fechaPrevista);
    Optional<EntradaPrevista> findByIdAndNegocioId(UUID id, UUID negocioId);
    List<EntradaPrevista> findByNegocioIdAndFechaPrevistaBetweenOrderByFechaPrevistaAsc(UUID negocioId, LocalDate desde, LocalDate hasta);

    @Query("SELECT e FROM EntradaPrevista e JOIN FETCH e.detalles WHERE e.negocio.id = :negocioId AND e.fechaPrevista = :fecha")
    List<EntradaPrevista> findByNegocioIdAndFechaWithDetails(@Param("negocioId") UUID negocioId, @Param("fecha") LocalDate fecha);
}
