package com.stockpro.dto.sale;

import com.stockpro.model.entity.DetalleVenta;

import java.math.BigDecimal;
import java.util.UUID;

public class DetalleVentaDto {
    private UUID id;
    private UUID productoId;
    private String productoNombre;
    private UUID loteId;
    private String numeroLote;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal costoUnitario;
    private BigDecimal subtotalBruto;
    private BigDecimal descuento;
    private String categoriaFiscal;
    private BigDecimal tarifaImpuestoPorcentaje;
    private BigDecimal montoImpuesto;
    private BigDecimal totalLinea;

    public DetalleVentaDto() {}

    public DetalleVentaDto(DetalleVenta d) {
        this.id = d.getId();
        if (d.getProducto() != null) {
            this.productoId = d.getProducto().getId();
            this.productoNombre = d.getProducto().getNombre();
        }
        if (d.getLote() != null) {
            this.loteId = d.getLote().getId();
            this.numeroLote = d.getLote().getNumeroLote();
        }
        this.cantidad = d.getCantidad();
        this.precioUnitario = d.getPrecioUnitario();
        this.costoUnitario = d.getCostoUnitario();
        this.subtotalBruto = d.getSubtotalBruto();
        this.descuento = d.getDescuento();
        this.categoriaFiscal = d.getCategoriaFiscal();
        this.tarifaImpuestoPorcentaje = d.getTarifaImpuestoPorcentaje();
        this.montoImpuesto = d.getMontoImpuesto();
        this.totalLinea = d.getTotalLinea();
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
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
    public BigDecimal getSubtotalBruto() { return subtotalBruto; }
    public void setSubtotalBruto(BigDecimal subtotalBruto) { this.subtotalBruto = subtotalBruto; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public String getCategoriaFiscal() { return categoriaFiscal; }
    public void setCategoriaFiscal(String categoriaFiscal) { this.categoriaFiscal = categoriaFiscal; }
    public BigDecimal getTarifaImpuestoPorcentaje() { return tarifaImpuestoPorcentaje; }
    public void setTarifaImpuestoPorcentaje(BigDecimal tarifaImpuestoPorcentaje) { this.tarifaImpuestoPorcentaje = tarifaImpuestoPorcentaje; }
    public BigDecimal getMontoImpuesto() { return montoImpuesto; }
    public void setMontoImpuesto(BigDecimal montoImpuesto) { this.montoImpuesto = montoImpuesto; }
    public BigDecimal getTotalLinea() { return totalLinea; }
    public void setTotalLinea(BigDecimal totalLinea) { this.totalLinea = totalLinea; }
}
