package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "productos", uniqueConstraints = {
    @UniqueConstraint(name = "uq_negocio_producto_nombre", columnNames = {"negocio_id", "nombre"})
})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida = "UNIDAD";

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal costo;

    @Column(name = "precio_venta", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "categoria_fiscal", nullable = false, length = 50)
    private String categoriaFiscal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarifa_impuesto_id")
    private TarifaImpuesto tarifaImpuesto;

    @Column(name = "porcentaje_impuesto_aplicado", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeImpuestoAplicado = BigDecimal.ZERO;

    @Column(name = "precio_incluye_impuesto", nullable = false)
    private Boolean precioIncluyeImpuesto = true;

    @Column(name = "stock_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal stockActual = BigDecimal.ZERO;

    @Column(name = "stock_minimo", nullable = false, precision = 12, scale = 2)
    private BigDecimal stockMinimo = new BigDecimal("5.00");

    @Column(name = "maneja_lotes", nullable = false)
    private Boolean manejaLotes = false;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Producto() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

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

    public TarifaImpuesto getTarifaImpuesto() { return tarifaImpuesto; }
    public void setTarifaImpuesto(TarifaImpuesto tarifaImpuesto) { this.tarifaImpuesto = tarifaImpuesto; }

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

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
