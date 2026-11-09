package com.stockpro.repository;

import com.stockpro.model.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, UUID> {
    List<MovimientoInventario> findByNegocioIdAndFechaMovimientoBetweenOrderByFechaMovimientoDesc(UUID negocioId, OffsetDateTime inicio, OffsetDateTime fin);
    List<MovimientoInventario> findByNegocioIdOrderByFechaMovimientoDesc(UUID negocioId);

    @Query("SELECT m FROM MovimientoInventario m LEFT JOIN FETCH m.producto WHERE m.negocio.id = :negocioId AND m.tipoMovimiento IN ('PERDIDA', 'DANADO', 'AJUSTE_NEGATIVO') AND m.fechaMovimiento BETWEEN :inicio AND :fin ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findPerdidasYBajas(@Param("negocioId") UUID negocioId, @Param("inicio") OffsetDateTime inicio, @Param("fin") OffsetDateTime fin);
}
