package com.stockpro.controller;

import com.stockpro.dto.lot.BajaLoteRequest;
import com.stockpro.dto.lot.CreateLoteRequest;
import com.stockpro.dto.lot.LoteDto;
import com.stockpro.dto.lot.VencimientoAlertDto;
import com.stockpro.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lots")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping("/product/{productoId}")
    public ResponseEntity<List<LoteDto>> listLotesByProduct(@PathVariable UUID productoId) {
        return ResponseEntity.ok(loteService.listLotesByProduct(productoId));
    }

    @PostMapping
    public ResponseEntity<?> createLote(@Valid @RequestBody CreateLoteRequest request) {
        try {
            LoteDto dto = loteService.createLote(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/baja")
    public ResponseEntity<?> darDeBajaLote(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) BajaLoteRequest request
    ) {
        try {
            BajaLoteRequest req = request != null ? request : new BajaLoteRequest();
            loteService.darDeBajaLote(id, req);
            return ResponseEntity.ok(Map.of("mensaje", "Lote dado de baja como pérdida e inventario actualizado"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<VencimientoAlertDto>> getProximosAVencer() {
        return ResponseEntity.ok(loteService.getProximosAVencer());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<VencimientoAlertDto>> getVencidos() {
        return ResponseEntity.ok(loteService.getVencidos());
    }
}
