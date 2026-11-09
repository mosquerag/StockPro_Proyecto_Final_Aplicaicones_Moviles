package com.stockpro.controller;

import com.stockpro.dto.movement.CreateMovimientoRequest;
import com.stockpro.dto.movement.MovimientoInventarioDto;
import com.stockpro.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory-movements")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    /**
     * Registra un movimiento de inventario (ENTRADA, PERDIDA, DANADO, AJUSTE_POSITIVO, AJUSTE_NEGATIVO, DEVOLUCION).
     */
    @PostMapping
    public ResponseEntity<MovimientoInventarioDto> createMovement(@Valid @RequestBody CreateMovimientoRequest request) {
        MovimientoInventarioDto creado = movimientoService.createMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Lista el historial cronológico de movimientos de inventario del negocio.
     */
    @GetMapping
    public ResponseEntity<List<MovimientoInventarioDto>> listMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fin) {
        List<MovimientoInventarioDto> movimientos = movimientoService.listMovements(inicio, fin);
        return ResponseEntity.ok(movimientos);
    }
}
