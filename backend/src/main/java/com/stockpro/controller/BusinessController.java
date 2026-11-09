package com.stockpro.controller;

import com.stockpro.dto.business.NegocioDto;
import com.stockpro.dto.business.UpdateNegocioRequest;
import com.stockpro.model.entity.Negocio;
import com.stockpro.repository.NegocioRepository;
import com.stockpro.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/business")
public class BusinessController {

    private final NegocioRepository negocioRepository;
    private final SecurityUtils securityUtils;

    public BusinessController(NegocioRepository negocioRepository, SecurityUtils securityUtils) {
        this.negocioRepository = negocioRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<NegocioDto> getBusiness() {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con ID: " + negocioId));
        return ResponseEntity.ok(new NegocioDto(negocio));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<NegocioDto> updateBusiness(@Valid @RequestBody UpdateNegocioRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con ID: " + negocioId));

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            negocio.setNombre(request.getNombre().trim());
        }
        if (request.getPais() != null && !request.getPais().isBlank()) {
            negocio.setPais(request.getPais().trim().toUpperCase());
        }
        if (request.getEstadoRegion() != null) {
            negocio.setEstadoRegion(request.getEstadoRegion().trim());
        }
        if (request.getMonedaBase() != null && !request.getMonedaBase().isBlank()) {
            negocio.setMonedaBase(request.getMonedaBase().trim().toUpperCase());
        }
        if (request.getTipoNegocio() != null && !request.getTipoNegocio().isBlank()) {
            negocio.setTipoNegocio(request.getTipoNegocio().trim());
        }
        if (request.getIdentificacionFiscal() != null) {
            negocio.setIdentificacionFiscal(request.getIdentificacionFiscal().trim());
        }
        if (request.getPermiteStockNegativo() != null) {
            negocio.setPermiteStockNegativo(request.getPermiteStockNegativo());
        }

        Negocio saved = negocioRepository.save(negocio);
        return ResponseEntity.ok(new NegocioDto(saved));
    }
}
