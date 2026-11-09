package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "entradas_previstas")
public class EntradaPrevista {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(name = "numero_orden", length = 50)
    private String numeroOrden;

    @Column(name = "fecha_prevista", nullable = false)
    private LocalDate fechaPrevista;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, RECIBIDA, PARCIAL, CANCELADA

    @Column(name = "total_estimado", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalEstimado = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_recibida")
    private OffsetDateTime fechaRecibida;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "entradaPrevista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleEntradaPrevista> detalles = new ArrayList<>();

    public EntradaPrevista() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public LocalDate getFechaPrevista() { return fechaPrevista; }
    public void setFechaPrevista(LocalDate fechaPrevista) { this.fechaPrevista = fechaPrevista; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotalEstimado() { return totalEstimado; }
    public void setTotalEstimado(BigDecimal totalEstimado) { this.totalEstimado = totalEstimado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public OffsetDateTime getFechaRecibida() { return fechaRecibida; }
    public void setFechaRecibida(OffsetDateTime fechaRecibida) { this.fechaRecibida = fechaRecibida; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public List<DetalleEntradaPrevista> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleEntradaPrevista> detalles) { this.detalles = detalles; }
}
