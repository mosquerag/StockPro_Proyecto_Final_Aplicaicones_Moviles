package com.stockpro.controller;

import com.stockpro.dto.report.ReporteGeneralDto;
import com.stockpro.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Genera los reportes con filtros de tiempo (7D, 30D, 3M) y opción de conversión
     * de moneda (COP, USD, EUR) sin alterar el histórico de las ventas originales.
     * Incluye:
     * - Ganancias (ventas, costos, ganancia bruta, margen porcentual)
     * - Productos más y menos vendidos
     * - Pérdidas, daños y bajas de inventario
     * - Productividad por producto
     * - Impuestos cobrados por categoría fiscal
     */
    @GetMapping
    public ResponseEntity<ReporteGeneralDto> getReport(
            @RequestParam(defaultValue = "30D") String period,
            @RequestParam(required = false) String currency) {
        ReporteGeneralDto report = reporteService.generateReport(period, currency);
        return ResponseEntity.ok(report);
    }
}
