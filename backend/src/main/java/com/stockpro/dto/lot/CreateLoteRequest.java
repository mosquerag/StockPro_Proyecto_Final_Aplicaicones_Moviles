package com.stockpro.dto.lot;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateLoteRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productoId;

    @NotBlank(message = "El número o identificador del lote es obligatorio")
    private String numeroLote;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;

    private LocalDate fechaVencimiento;

    private String motivo = "Ingreso de nuevo lote al inventario";

    public CreateLoteRequest() {}

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
