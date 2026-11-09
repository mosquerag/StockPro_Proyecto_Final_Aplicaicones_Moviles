package com.stockpro.dto.movement;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateMovimientoRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productoId;

    private UUID loteId;

    @NotBlank(message = "El tipo de movimiento es obligatorio (ENTRADA, PERDIDA, DANADO, AJUSTE_POSITIVO, AJUSTE_NEGATIVO, DEVOLUCION)")
    private String tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    @NotBlank(message = "El motivo del movimiento es obligatorio")
    private String motivo;

    public CreateMovimientoRequest() {}

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public UUID getLoteId() { return loteId; }
    public void setLoteId(UUID loteId) { this.loteId = loteId; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
