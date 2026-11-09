package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "costo_unitario", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "subtotal_bruto", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotalBruto;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "categoria_fiscal", nullable = false, length = 50)
    private String categoriaFiscal;

    @Column(name = "tarifa_impuesto_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal tarifaImpuestoPorcentaje;

    @Column(name = "monto_impuesto", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    @Column(name = "total_linea", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalLinea;

    public DetalleVenta() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

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
