package com.stockpro.repository;

import com.stockpro.model.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LoteRepository extends JpaRepository<Lote, UUID> {
    List<Lote> findByProductoIdAndEstadoOrderByFechaVencimientoAsc(UUID productoId, String estado);

    // Lotes próximos a vencer (dentro de los próximos 7 días a partir de hoy)
    @Query("SELECT l FROM Lote l WHERE l.negocio.id = :negocioId AND l.cantidadDisponible > 0 AND l.fechaVencimiento BETWEEN :hoy AND :limite AND l.estado = 'ACTIVO' ORDER BY l.fechaVencimiento ASC")
    List<Lote> findProximosAVencer(@Param("negocioId") UUID negocioId, @Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);

    // Lotes ya vencidos
    @Query("SELECT l FROM Lote l WHERE l.negocio.id = :negocioId AND l.cantidadDisponible > 0 AND l.fechaVencimiento < :hoy ORDER BY l.fechaVencimiento ASC")
    List<Lote> findVencidos(@Param("negocioId") UUID negocioId, @Param("hoy") LocalDate hoy);
}
