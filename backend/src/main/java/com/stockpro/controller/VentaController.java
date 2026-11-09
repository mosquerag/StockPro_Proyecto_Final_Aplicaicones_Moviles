package com.stockpro.controller;

import com.stockpro.dto.sale.CreateVentaRequest;
import com.stockpro.dto.sale.VentaDto;
import com.stockpro.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Registra una venta transaccional con descuento de stock FEFO y cálculo fiscal 2026.
     */
    @PostMapping
    public ResponseEntity<VentaDto> createSale(@Valid @RequestBody CreateVentaRequest request) {
        VentaDto creada = ventaService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Consulta el historial de ventas del negocio actual con rango opcional de fechas.
     */
    @GetMapping
    public ResponseEntity<List<VentaDto>> listSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fin) {
        List<VentaDto> ventas = ventaService.listVentas(inicio, fin);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene el detalle completo de una venta por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaDto> getSaleById(@PathVariable UUID id) {
        VentaDto venta = ventaService.getVentaById(id);
        return ResponseEntity.ok(venta);
    }
}
