package com.stockpro.repository;

import com.stockpro.model.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VentaRepository extends JpaRepository<Venta, UUID> {
    List<Venta> findByNegocioIdAndFechaVentaBetweenOrderByFechaVentaDesc(UUID negocioId, OffsetDateTime inicio, OffsetDateTime fin);

    @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v WHERE v.negocio.id = :negocioId AND v.fechaVenta >= :desde AND v.estado = 'COMPLETADA'")
    BigDecimal sumVentasDesde(@Param("negocioId") UUID negocioId, @Param("desde") OffsetDateTime desde);

    @Query("SELECT v FROM Venta v WHERE v.negocio.id = :negocioId ORDER BY v.fechaVenta DESC LIMIT 5")
    List<Venta> findUltimasVentas(@Param("negocioId") UUID negocioId);

    @Query("SELECT DISTINCT v FROM Venta v LEFT JOIN FETCH v.detalles d LEFT JOIN FETCH d.producto WHERE v.negocio.id = :negocioId AND v.fechaVenta BETWEEN :inicio AND :fin AND v.estado = 'COMPLETADA' ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasCompletadasConDetalles(@Param("negocioId") UUID negocioId, @Param("inicio") OffsetDateTime inicio, @Param("fin") OffsetDateTime fin);
}
