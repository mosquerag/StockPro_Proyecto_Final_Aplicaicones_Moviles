package com.stockpro.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class UpdateProductoRequest {

    @NotNull(message = "La categoría es obligatoria")
    private UUID categoriaId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String codigoBarras;

    private String unidadMedida;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    private BigDecimal costo;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioVenta;

    @NotBlank(message = "La categoría fiscal es obligatoria")
    private String categoriaFiscal;

    private UUID tarifaImpuestoId;

    private Boolean precioIncluyeImpuesto;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0", message = "El stock mínimo no puede ser negativo")
    private BigDecimal stockMinimo;

    private Boolean manejaLotes;

    private String imagenUrl;

    private BigDecimal stockActual;

    private LocalDate fechaVencimiento;

    private Boolean activo;

    // NOTA DE REGLA DE NEGOCIO:
    // El campo stockActual NO existe en esta petición. El stock nunca se edita a mano,
    // únicamente se altera mediante movimientos de inventario o recepción de lotes.

    public UpdateProductoRequest() {}

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
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public BigDecimal getStockActual() { return stockActual; }
    public void setStockActual(BigDecimal stockActual) { this.stockActual = stockActual; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
