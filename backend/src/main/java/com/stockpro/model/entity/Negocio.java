package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "negocios")
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "tipo_negocio", nullable = false, length = 50)
    private String tipoNegocio;

    @Column(name = "identificacion_fiscal", length = 50)
    private String identificacionFiscal;

    @Column(nullable = false, length = 2)
    private String pais;

    @Column(name = "estado_region", length = 100)
    private String estadoRegion;

    @Column(name = "moneda_base", nullable = false, length = 3)
    private String monedaBase;

    @Column(name = "zona_horaria", nullable = false, length = 50)
    private String zonaHoraria = "America/Bogota";

    @Column(name = "permite_stock_negativo", nullable = false)
    private Boolean permiteStockNegativo = false;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Negocio() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }

    public String getIdentificacionFiscal() { return identificacionFiscal; }
    public void setIdentificacionFiscal(String identificacionFiscal) { this.identificacionFiscal = identificacionFiscal; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstadoRegion() { return estadoRegion; }
    public void setEstadoRegion(String estadoRegion) { this.estadoRegion = estadoRegion; }

    public String getMonedaBase() { return monedaBase; }
    public void setMonedaBase(String monedaBase) { this.monedaBase = monedaBase; }

    public String getZonaHoraria() { return zonaHoraria; }
    public void setZonaHoraria(String zonaHoraria) { this.zonaHoraria = zonaHoraria; }

    public Boolean getPermiteStockNegativo() { return permiteStockNegativo; }
    public void setPermiteStockNegativo(Boolean permiteStockNegativo) { this.permiteStockNegativo = permiteStockNegativo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
