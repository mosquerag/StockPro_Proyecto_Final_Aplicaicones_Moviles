package com.stockpro.repository;

import com.stockpro.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    List<Categoria> findByNegocioIdAndActivoTrueOrderByNombreAsc(UUID negocioId);
    Optional<Categoria> findByNegocioIdAndNombreIgnoreCase(UUID negocioId, String nombre);
}
