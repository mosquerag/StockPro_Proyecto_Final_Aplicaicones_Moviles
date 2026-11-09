package com.stockpro.controller;

import com.stockpro.dto.tax.TarifaImpuestoDto;
import com.stockpro.model.entity.Negocio;
import com.stockpro.repository.NegocioRepository;
import com.stockpro.repository.TarifaImpuestoRepository;
import com.stockpro.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tax-rates")
public class TaxRateController {

    private final TarifaImpuestoRepository tarifaImpuestoRepository;
    private final NegocioRepository negocioRepository;
    private final SecurityUtils securityUtils;

    public TaxRateController(TarifaImpuestoRepository tarifaImpuestoRepository,
                             NegocioRepository negocioRepository,
                             SecurityUtils securityUtils) {
        this.tarifaImpuestoRepository = tarifaImpuestoRepository;
        this.negocioRepository = negocioRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<TarifaImpuestoDto>> getTaxRates(@RequestParam(required = false) String country) {
        String targetCountry = country;
        if (targetCountry == null || targetCountry.isBlank()) {
            UUID negocioId = securityUtils.getCurrentNegocioId();
            Negocio negocio = negocioRepository.findById(negocioId)
                    .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con ID: " + negocioId));
            targetCountry = negocio.getPais();
        }

        List<TarifaImpuestoDto> rates = tarifaImpuestoRepository
                .findByPaisAndActivoTrueOrderByPorcentajeAsc(targetCountry.toUpperCase())
                .stream()
                .map(TarifaImpuestoDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(rates);
    }
}
