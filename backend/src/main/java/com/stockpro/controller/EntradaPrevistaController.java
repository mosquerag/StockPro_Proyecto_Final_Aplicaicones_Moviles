package com.stockpro.controller;

import com.stockpro.dto.inbound.CreateEntradaPrevistaRequest;
import com.stockpro.dto.inbound.EntradaPrevistaDto;
import com.stockpro.dto.inbound.ReceiveEntradaRequest;
import com.stockpro.service.EntradaPrevistaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inbound-deliveries")
public class EntradaPrevistaController {

    private final EntradaPrevistaService entradaService;

    public EntradaPrevistaController(EntradaPrevistaService entradaService) {
        this.entradaService = entradaService;
    }

    /**
     * Crea una entrada prevista (hoy, mañana u otra fecha) con productos y cantidades.
     * No afecta el stock hasta que sea recibida.
     */
    @PostMapping
    public ResponseEntity<EntradaPrevistaDto> createEntrada(@Valid @RequestBody CreateEntradaPrevistaRequest request) {
        EntradaPrevistaDto creada = entradaService.createEntrada(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Lista las entradas previstas para el día de hoy.
     */
    @GetMapping("/today")
    public ResponseEntity<List<EntradaPrevistaDto>> listToday() {
        return ResponseEntity.ok(entradaService.listHoy());
    }

    /**
     * Lista las entradas previstas para el día de mañana.
     */
    @GetMapping("/tomorrow")
    public ResponseEntity<List<EntradaPrevistaDto>> listTomorrow() {
        return ResponseEntity.ok(entradaService.listManana());
    }

    /**
     * Consulta una entrada prevista por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntradaPrevistaDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(entradaService.getById(id));
    }

    /**
     * Marca la entrada prevista como RECIBIDA:
     * Crea el lote (con vencimiento opcional), suma el stock y genera el movimiento ENTRADA.
     */
    @PostMapping("/{id}/receive")
    public ResponseEntity<EntradaPrevistaDto> receiveEntrada(
            @PathVariable UUID id,
            @RequestBody(required = false) ReceiveEntradaRequest request) {
        EntradaPrevistaDto recibida = entradaService.receiveEntrada(id, request);
        return ResponseEntity.ok(recibida);
    }
}
