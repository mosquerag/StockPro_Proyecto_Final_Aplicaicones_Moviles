package com.stockpro.dto.sale;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateVentaItemRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    private BigDecimal precioUnitario; // Opcional: si es nulo, toma el precio de venta configurado en el producto

    private BigDecimal descuento = BigDecimal.ZERO;

    public CreateVentaItemRequest() {}

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}
