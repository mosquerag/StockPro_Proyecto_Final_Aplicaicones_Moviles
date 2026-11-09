package com.stockpro.dto.lot;

import com.stockpro.model.entity.Lote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class LoteDto {
    private UUID id;
    private UUID productoId;
    private String productoNombre;
    private UUID negocioId;
    private String numeroLote;
    private BigDecimal cantidadInicial;
    private BigDecimal cantidadDisponible;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
    private String estado;
    private OffsetDateTime createdAt;

    public LoteDto() {}

    public LoteDto(Lote lote) {
        this.id = lote.getId();
        if (lote.getProducto() != null) {
            this.productoId = lote.getProducto().getId();
            this.productoNombre = lote.getProducto().getNombre();
        }
        if (lote.getNegocio() != null) {
            this.negocioId = lote.getNegocio().getId();
        }
        this.numeroLote = lote.getNumeroLote();
        this.cantidadInicial = lote.getCantidadInicial();
        this.cantidadDisponible = lote.getCantidadDisponible();
        this.fechaIngreso = lote.getFechaIngreso();
        this.fechaVencimiento = lote.getFechaVencimiento();
        this.estado = lote.getEstado();
        this.createdAt = lote.getCreatedAt();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public UUID getNegocioId() { return negocioId; }
    public void setNegocioId(UUID negocioId) { this.negocioId = negocioId; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public BigDecimal getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(BigDecimal cantidadInicial) { this.cantidadInicial = cantidadInicial; }
    public BigDecimal getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(BigDecimal cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
