package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private UUID entidadId;

    @Column(name = "detalles_json", columnDefinition = "jsonb")
    private String detallesJson;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Auditoria() {}

    public Auditoria(Negocio negocio, Usuario usuario, String accion, String entidad, UUID entidadId, String detallesJson, String ipOrigen) {
        this.negocio = negocio;
        this.usuario = usuario;
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.detallesJson = detallesJson;
        this.ipOrigen = ipOrigen;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public UUID getEntidadId() { return entidadId; }
    public void setEntidadId(UUID entidadId) { this.entidadId = entidadId; }

    public String getDetallesJson() { return detallesJson; }
    public void setDetallesJson(String detallesJson) { this.detallesJson = detallesJson; }

    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
