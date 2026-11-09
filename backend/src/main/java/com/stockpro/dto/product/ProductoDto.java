package com.stockpro.dto.product;

import com.stockpro.model.entity.Producto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ProductoDto {
    private UUID id;
    private UUID negocioId;
    private UUID categoriaId;
    private String categoriaNombre;
    private String nombre;
    private String codigoBarras;
    private String unidadMedida;
    private BigDecimal costo;
    private BigDecimal precioVenta;
    private String categoriaFiscal;
    private UUID tarifaImpuestoId;
    private BigDecimal porcentajeImpuestoAplicado;
    private Boolean precioIncluyeImpuesto;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private Boolean manejaLotes;
    private Boolean stockBajo;
    private Boolean activo;
    private OffsetDateTime createdAt;

    public ProductoDto() {}

    public ProductoDto(Producto p) {
        this.id = p.getId();
        this.negocioId = p.getNegocio().getId();
        if (p.getCategoria() != null) {
            this.categoriaId = p.getCategoria().getId();
            this.categoriaNombre = p.getCategoria().getNombre();
        }
        this.nombre = p.getNombre();
        this.codigoBarras = p.getCodigoBarras();
        this.unidadMedida = p.getUnidadMedida();
        this.costo = p.getCosto();
        this.precioVenta = p.getPrecioVenta();
        this.categoriaFiscal = p.getCategoriaFiscal();
        if (p.getTarifaImpuesto() != null) {
            this.tarifaImpuestoId = p.getTarifaImpuesto().getId();
        }
        this.porcentajeImpuestoAplicado = p.getPorcentajeImpuestoAplicado();
        this.precioIncluyeImpuesto = p.getPrecioIncluyeImpuesto();
        this.stockActual = p.getStockActual();
        this.stockMinimo = p.getStockMinimo();
        this.manejaLotes = p.getManejaLotes();
        this.stockBajo = p.getStockActual().compareTo(p.getStockMinimo()) <= 0;
        this.activo = p.getActivo();
        this.createdAt = p.getCreatedAt();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getNegocioId() { return negocioId; }
    public void setNegocioId(UUID negocioId) { this.negocioId = negocioId; }
    public UUID getCategoriaId() { return categoriaId; }
    public void setCategoriaId(UUID categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
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
    public BigDecimal getPorcentajeImpuestoAplicado() { return porcentajeImpuestoAplicado; }
    public void setPorcentajeImpuestoAplicado(BigDecimal porcentajeImpuestoAplicado) { this.porcentajeImpuestoAplicado = porcentajeImpuestoAplicado; }
    public Boolean getPrecioIncluyeImpuesto() { return precioIncluyeImpuesto; }
    public void setPrecioIncluyeImpuesto(Boolean precioIncluyeImpuesto) { this.precioIncluyeImpuesto = precioIncluyeImpuesto; }
    public BigDecimal getStockActual() { return stockActual; }
    public void setStockActual(BigDecimal stockActual) { this.stockActual = stockActual; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }
    public Boolean getManejaLotes() { return manejaLotes; }
    public void setManejaLotes(Boolean manejaLotes) { this.manejaLotes = manejaLotes; }
    public Boolean getStockBajo() { return stockBajo; }
    public void setStockBajo(Boolean stockBajo) { this.stockBajo = stockBajo; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
