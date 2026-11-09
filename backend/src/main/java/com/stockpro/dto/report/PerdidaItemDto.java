package com.stockpro.dto.report;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PerdidaItemDto {

    private UUID movimientoId;
    private UUID productoId;
    private String productoNombre;
    private String tipoMovimiento; // PERDIDA, DANADO, AJUSTE_NEGATIVO
    private BigDecimal cantidad;
    private BigDecimal costoReferencia;
    private BigDecimal costoTotalPerdido;
    private String motivo;
    private OffsetDateTime fecha;

    public PerdidaItemDto() {}

    public UUID getMovimientoId() { return movimientoId; }
    public void setMovimientoId(UUID movimientoId) { this.movimientoId = movimientoId; }

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getCostoReferencia() { return costoReferencia; }
    public void setCostoReferencia(BigDecimal costoReferencia) { this.costoReferencia = costoReferencia; }

    public BigDecimal getCostoTotalPerdido() { return costoTotalPerdido; }
    public void setCostoTotalPerdido(BigDecimal costoTotalPerdido) { this.costoTotalPerdido = costoTotalPerdido; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
}
