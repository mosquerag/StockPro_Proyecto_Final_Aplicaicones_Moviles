package com.stockpro.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateProductoRequest {

    @NotNull(message = "La categoría es obligatoria")
    private UUID categoriaId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String codigoBarras;

    private String unidadMedida = "UNIDAD";

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    private BigDecimal costo;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioVenta;

    @NotBlank(message = "La categoría fiscal es obligatoria (ej. GENERAL, EXCLUIDO, etc.)")
    private String categoriaFiscal;

    private UUID tarifaImpuestoId;

    private Boolean precioIncluyeImpuesto = true;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0", message = "El stock mínimo no puede ser negativo")
    private BigDecimal stockMinimo = new BigDecimal("5.00");

    private Boolean manejaLotes = false;

    // Campos opcionales para ingreso de inventario inicial transaccional
    private BigDecimal cantidadInicial;
    private String numeroLoteInicial;
    private LocalDate fechaVencimientoInicial;

    public CreateProductoRequest() {}

    public UUID getCategoriaId() { return categoriaId; }
    public void setCategoriaId(UUID categoriaId) { this.categoriaId = categoriaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public String getCategoriaFiscal() { return categoriaFiscal; }
    public void setCategoriaFiscal(String categoriaFiscal) { this.categoriaFiscal = categoriaFiscal; }
    public UUID getTarifaImpuestoId() { return tarifaImpuestoId; }
    public void setTarifaImpuestoId(UUID tarifaImpuestoId) { this.tarifaImpuestoId = tarifaImpuestoId; }
    public Boolean getPrecioIncluyeImpuesto() { return precioIncluyeImpuesto; }
    public void setPrecioIncluyeImpuesto(Boolean precioIncluyeImpuesto) { this.precioIncluyeImpuesto = precioIncluyeImpuesto; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }
    public Boolean getManejaLotes() { return manejaLotes; }
    public void setManejaLotes(Boolean manejaLotes) { this.manejaLotes = manejaLotes; }
    public BigDecimal getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(BigDecimal cantidadInicial) { this.cantidadInicial = cantidadInicial; }
    public String getNumeroLoteInicial() { return numeroLoteInicial; }
    public void setNumeroLoteInicial(String numeroLoteInicial) { this.numeroLoteInicial = numeroLoteInicial; }
    public LocalDate getFechaVencimientoInicial() { return fechaVencimientoInicial; }
    public void setFechaVencimientoInicial(LocalDate fechaVencimientoInicial) { this.fechaVencimientoInicial = fechaVencimientoInicial; }
}
