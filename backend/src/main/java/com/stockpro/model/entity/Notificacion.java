package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @Column(nullable = false, length = 30)
    private String tipo; // STOCK_BAJO, PROXIMO_VENCER, VENCIDO, ENTRADA_PREVISTA, SISTEMA

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Notificacion() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public UUID getReferenciaId() { return referenciaId; }
    public void setReferenciaId(UUID referenciaId) { this.referenciaId = referenciaId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
