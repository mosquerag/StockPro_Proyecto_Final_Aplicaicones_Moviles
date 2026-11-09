package com.stockpro.controller;

import com.stockpro.dto.dashboard.DashboardDto;
import com.stockpro.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Entrega las métricas consolidadas del inicio:
     * - Ventas de hoy y ganancias estimadas
     * - Cantidad total de productos
     * - Resumen y listado de alertas (stock bajo, por vencer a 7 días, vencidos)
     * - Últimas 5 ventas registradas
     * - Entradas previstas de hoy y de mañana
     */
    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard(
            @RequestParam(required = false) String currency) {
        DashboardDto dashboard = dashboardService.getDashboard(currency);
        return ResponseEntity.ok(dashboard);
    }
}
