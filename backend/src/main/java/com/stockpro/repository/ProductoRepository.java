package com.stockpro.repository;

import com.stockpro.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    List<Producto> findByNegocioIdAndActivoTrueOrderByNombreAsc(UUID negocioId);
    Optional<Producto> findByIdAndNegocioId(UUID id, UUID negocioId);

    @Query("SELECT p FROM Producto p WHERE p.negocio.id = :negocioId AND p.activo = true AND p.stockActual <= p.stockMinimo ORDER BY p.stockActual ASC")
    List<Producto> findStockBajo(@Param("negocioId") UUID negocioId);

    long countByNegocioIdAndActivoTrue(UUID negocioId);
}
