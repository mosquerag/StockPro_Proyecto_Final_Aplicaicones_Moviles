package com.stockpro.dto.inbound;

import com.stockpro.model.entity.EntradaPrevista;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntradaPrevistaDto {

    private UUID id;
    private UUID negocioId;
    private String numeroOrden; // Contiene también la referencia / origen del envío
    private LocalDate fechaPrevista;
    private String estado; // PENDIENTE, RECIBIDA, CANCELADA
    private BigDecimal totalEstimado;
    private String notas;
    private OffsetDateTime fechaRecibida;
    private OffsetDateTime createdAt;
    private List<DetalleEntradaPrevistaDto> items = new ArrayList<>();

    public EntradaPrevistaDto() {}

    public EntradaPrevistaDto(EntradaPrevista e) {
        this.id = e.getId();
        if (e.getNegocio() != null) {
            this.negocioId = e.getNegocio().getId();
        }
        this.numeroOrden = e.getNumeroOrden();
        this.fechaPrevista = e.getFechaPrevista();
        this.estado = e.getEstado();
        this.totalEstimado = e.getTotalEstimado();
        this.notas = e.getNotas();
        this.fechaRecibida = e.getFechaRecibida();
        this.createdAt = e.getCreatedAt();
        if (e.getDetalles() != null) {
            this.items = e.getDetalles().stream()
                    .map(DetalleEntradaPrevistaDto::new)
                    .collect(Collectors.toList());
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getNegocioId() { return negocioId; }
    public void setNegocioId(UUID negocioId) { this.negocioId = negocioId; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public LocalDate getFechaPrevista() { return fechaPrevista; }
    public void setFechaPrevista(LocalDate fechaPrevista) { this.fechaPrevista = fechaPrevista; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotalEstimado() { return totalEstimado; }
    public void setTotalEstimado(BigDecimal totalEstimado) { this.totalEstimado = totalEstimado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public OffsetDateTime getFechaRecibida() { return fechaRecibida; }
    public void setFechaRecibida(OffsetDateTime fechaRecibida) { this.fechaRecibida = fechaRecibida; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public List<DetalleEntradaPrevistaDto> getItems() { return items; }
    public void setItems(List<DetalleEntradaPrevistaDto> items) { this.items = items; }
}
