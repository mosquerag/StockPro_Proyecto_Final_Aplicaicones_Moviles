package com.stockpro.dto.inbound;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateDetalleEntradaRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productoId;

    @NotNull(message = "La cantidad esperada es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad esperada debe ser mayor a 0")
    private BigDecimal cantidadEsperada;

    private BigDecimal costoUnitario;

    public CreateDetalleEntradaRequest() {}

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public BigDecimal getCantidadEsperada() { return cantidadEsperada; }
    public void setCantidadEsperada(BigDecimal cantidadEsperada) { this.cantidadEsperada = cantidadEsperada; }

    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
}
