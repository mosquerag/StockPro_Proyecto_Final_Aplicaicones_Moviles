package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(name = "tipo_movimiento", nullable = false, length = 30)
    private String tipoMovimiento; // VENTA, ENTRADA, PERDIDA, DANADO, AJUSTE_POSITIVO, AJUSTE_NEGATIVO, DEVOLUCION

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "stock_anterior", nullable = false, precision = 12, scale = 2)
    private BigDecimal stockAnterior;

    @Column(name = "stock_posterior", nullable = false, precision = 12, scale = 2)
    private BigDecimal stockPosterior;

    @Column(name = "costo_referencia", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoReferencia = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo; // VENTA, ENTRADA_PREVISTA, AJUSTE_MANUAL, BAJA_VENCIMIENTO

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(name = "fecha_movimiento", nullable = false)
    private OffsetDateTime fechaMovimiento = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public MovimientoInventario() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(BigDecimal stockAnterior) { this.stockAnterior = stockAnterior; }

    public BigDecimal getStockPosterior() { return stockPosterior; }
    public void setStockPosterior(BigDecimal stockPosterior) { this.stockPosterior = stockPosterior; }

    public BigDecimal getCostoReferencia() { return costoReferencia; }
    public void setCostoReferencia(BigDecimal costoReferencia) { this.costoReferencia = costoReferencia; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    public UUID getReferenciaId() { return referenciaId; }
    public void setReferenciaId(UUID referenciaId) { this.referenciaId = referenciaId; }

    public OffsetDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(OffsetDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
