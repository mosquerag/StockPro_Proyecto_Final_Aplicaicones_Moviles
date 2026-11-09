package com.stockpro.dto.inbound;

import com.stockpro.model.entity.DetalleEntradaPrevista;

import java.math.BigDecimal;
import java.util.UUID;

public class DetalleEntradaPrevistaDto {

    private UUID id;
    private UUID productoId;
    private String productoNombre;
    private BigDecimal cantidadEsperada;
    private BigDecimal cantidadRecibida;
    private BigDecimal costoUnitario;
    private BigDecimal subtotalEsperado;

    public DetalleEntradaPrevistaDto() {}

    public DetalleEntradaPrevistaDto(DetalleEntradaPrevista d) {
        this.id = d.getId();
        if (d.getProducto() != null) {
            this.productoId = d.getProducto().getId();
            this.productoNombre = d.getProducto().getNombre();
        }
        this.cantidadEsperada = d.getCantidadEsperada();
        this.cantidadRecibida = d.getCantidadRecibida();
        this.costoUnitario = d.getCostoUnitario();
        this.subtotalEsperado = d.getCantidadEsperada() != null && d.getCostoUnitario() != null
                ? d.getCantidadEsperada().multiply(d.getCostoUnitario())
                : BigDecimal.ZERO;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public BigDecimal getCantidadEsperada() { return cantidadEsperada; }
    public void setCantidadEsperada(BigDecimal cantidadEsperada) { this.cantidadEsperada = cantidadEsperada; }

    public BigDecimal getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(BigDecimal cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }

    public BigDecimal getSubtotalEsperado() { return subtotalEsperado; }
    public void setSubtotalEsperado(BigDecimal subtotalEsperado) { this.subtotalEsperado = subtotalEsperado; }
}
