package com.stockpro.repository;

import com.stockpro.model.entity.TarifaImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TarifaImpuestoRepository extends JpaRepository<TarifaImpuesto, UUID> {
    List<TarifaImpuesto> findByPaisAndActivoTrueOrderByPorcentajeAsc(String pais);
    Optional<TarifaImpuesto> findFirstByPaisAndCategoriaFiscalAndActivoTrue(String pais, String categoriaFiscal);
    Optional<TarifaImpuesto> findFirstByPaisAndEstadoRegionAndCategoriaFiscalAndActivoTrue(String pais, String estadoRegion, String categoriaFiscal);
    List<TarifaImpuesto> findByPaisAndEstadoRegionAndActivoTrueOrderByPorcentajeAsc(String pais, String estadoRegion);
}
