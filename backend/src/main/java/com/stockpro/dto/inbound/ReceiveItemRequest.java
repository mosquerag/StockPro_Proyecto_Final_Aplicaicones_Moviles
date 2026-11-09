package com.stockpro.dto.inbound;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ReceiveItemRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productoId;

    @NotNull(message = "La cantidad recibida es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad recibida debe ser mayor a 0")
    private BigDecimal cantidadRecibida;

    // Fecha de vencimiento opcional si es perecedero o maneja lotes
    private LocalDate fechaVencimiento;

    // Número de lote opcional (si es nulo o vacío, el sistema genera uno automáticamente)
    private String numeroLote;

    public ReceiveItemRequest() {}

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public BigDecimal getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(BigDecimal cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
}
