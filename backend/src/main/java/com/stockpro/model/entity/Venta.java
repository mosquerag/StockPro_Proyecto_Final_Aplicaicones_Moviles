package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "numero_comprobante", nullable = false, length = 50)
    private String numeroComprobante;

    @Column(name = "fecha_venta", nullable = false)
    private OffsetDateTime fechaVenta = OffsetDateTime.now();

    @Column(name = "moneda_venta", nullable = false, length = 3)
    private String monedaVenta; // COP, USD, EUR

    @Column(name = "tasa_cambio_base", nullable = false, precision = 16, scale = 6)
    private BigDecimal tasaCambioBase = BigDecimal.ONE;

    @Column(name = "subtotal_base", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotalBase = BigDecimal.ZERO;

    @Column(name = "total_impuestos", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalImpuestos = BigDecimal.ZERO;

    @Column(name = "total_descuentos", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDescuentos = BigDecimal.ZERO;

    @Column(name = "total_venta", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalVenta = BigDecimal.ZERO;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago = "EFECTIVO"; // EFECTIVO, TARJETA, TRANSFERENCIA, MIXTO, OTRO

    @Column(nullable = false, length = 20)
    private String estado = "COMPLETADA"; // COMPLETADA, ANULADA

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

    public OffsetDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(OffsetDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public String getMonedaVenta() { return monedaVenta; }
    public void setMonedaVenta(String monedaVenta) { this.monedaVenta = monedaVenta; }

    public BigDecimal getTasaCambioBase() { return tasaCambioBase; }
    public void setTasaCambioBase(BigDecimal tasaCambioBase) { this.tasaCambioBase = tasaCambioBase; }

    public BigDecimal getSubtotalBase() { return subtotalBase; }
    public void setSubtotalBase(BigDecimal subtotalBase) { this.subtotalBase = subtotalBase; }

    public BigDecimal getTotalImpuestos() { return totalImpuestos; }
    public void setTotalImpuestos(BigDecimal totalImpuestos) { this.totalImpuestos = totalImpuestos; }

    public BigDecimal getTotalDescuentos() { return totalDescuentos; }
    public void setTotalDescuentos(BigDecimal totalDescuentos) { this.totalDescuentos = totalDescuentos; }

    public BigDecimal getTotalVenta() { return totalVenta; }
    public void setTotalVenta(BigDecimal totalVenta) { this.totalVenta = totalVenta; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}
