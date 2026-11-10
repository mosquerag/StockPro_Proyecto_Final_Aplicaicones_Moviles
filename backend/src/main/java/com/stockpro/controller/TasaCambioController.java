package com.stockpro.controller;

import com.stockpro.dto.currency.ManualTasaCambioRequest;
import com.stockpro.dto.currency.TasaCambioDto;
import com.stockpro.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange-rates")
public class TasaCambioController {

    private final CurrencyService currencyService;

    public TasaCambioController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * Obtiene el catálogo completo de tasas de cambio registradas en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<TasaCambioDto>> getAllRates() {
        return ResponseEntity.ok(currencyService.listAllRates());
    }

    /**
     * Refresca las tasas de cambio consumiendo la API pública internacional.
     */
    @PostMapping("/refresh")
    public ResponseEntity<List<TasaCambioDto>> refreshRates() {
        List<TasaCambioDto> actualizadas = currencyService.refreshRatesFromApi();
        return ResponseEntity.ok(actualizadas);
    }

    /**
     * Permite al usuario fijar una tasa manual si la API no está disponible o requiere tasa personalizada.
     */
    @PostMapping("/manual")
    public ResponseEntity<TasaCambioDto> setManualRate(@Valid @RequestBody ManualTasaCambioRequest request) {
        TasaCambioDto configurada = currencyService.setManualRate(request);
        return ResponseEntity.ok(configurada);
    }
}