package com.stockpro.dto.movement;

import com.stockpro.model.entity.MovimientoInventario;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MovimientoInventarioDto {
    private UUID id;
    private UUID productoId;
    private String productoNombre;
    private UUID loteId;
    private String numeroLote;
    private String tipoMovimiento;
    private BigDecimal cantidad;
    private BigDecimal stockAnterior;
    private BigDecimal stockPosterior;
    private BigDecimal costoReferencia;
    private String motivo;
    private String usuarioNombre;
    private String referenciaTipo;
    private UUID referenciaId;
    private OffsetDateTime fechaMovimiento;

    public MovimientoInventarioDto() {}

    public MovimientoInventarioDto(MovimientoInventario m) {
        this.id = m.getId();
        if (m.getProducto() != null) {
            this.productoId = m.getProducto().getId();
            this.productoNombre = m.getProducto().getNombre();
        }
        if (m.getLote() != null) {
            this.loteId = m.getLote().getId();
            this.numeroLote = m.getLote().getNumeroLote();
        }
        this.tipoMovimiento = m.getTipoMovimiento();
        this.cantidad = m.getCantidad();
        this.stockAnterior = m.getStockAnterior();
        this.stockPosterior = m.getStockPosterior();
        this.costoReferencia = m.getCostoReferencia();
        this.motivo = m.getMotivo();
        if (m.getUsuario() != null) {
            this.usuarioNombre = m.getUsuario().getNombre();
        }
        this.referenciaTipo = m.getReferenciaTipo();
        this.referenciaId = m.getReferenciaId();
        this.fechaMovimiento = m.getFechaMovimiento();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public UUID getLoteId() { return loteId; }
    public void setLoteId(UUID loteId) { this.loteId = loteId; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public BigDecimal getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(BigDecimal stockAnterior) { this.stockAnterior = stockAnterior; }
    public BigDecimal getStockPosterior() { return stockPosterior; }
    public void setStockPosterior(BigDecimal stockPosterior) { this.stockPosterior = stockPosterior; }
    public BigDecimal getCostoReferencia() { return costoReferencia; }
    public void setCostoReferencia(BigDecimal costoReferencia) { this.costoReferencia = costoReferencia; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }
    public UUID getReferenciaId() { return referenciaId; }
    public void setReferenciaId(UUID referenciaId) { this.referenciaId = referenciaId; }
    public OffsetDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(OffsetDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
}
