package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "miembros_negocio", uniqueConstraints = {
    @UniqueConstraint(name = "uq_negocio_usuario", columnNames = {"negocio_id", "usuario_id"})
})
public class MiembroNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 30)
    private String rol; // PROPIETARIO, ADMINISTRADOR, EMPLEADO, CONSULTA

    @Column(columnDefinition = "jsonb")
    private String permisos = "[]";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public MiembroNegocio() {}

    public MiembroNegocio(Negocio negocio, Usuario usuario, String rol) {
        this.negocio = negocio;
        this.usuario = usuario;
        this.rol = rol;
        this.permisos = "PROPIETARIO".equalsIgnoreCase(rol) ? "[\"ALL\"]" : "[\"VENTAS\", \"MOVIMIENTOS_ENTRADA\"]";
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getPermisos() { return permisos; }
    public void setPermisos(String permisos) { this.permisos = permisos; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
