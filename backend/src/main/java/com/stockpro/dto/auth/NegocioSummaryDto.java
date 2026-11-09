package com.stockpro.dto.auth;

import java.util.UUID;

public class NegocioSummaryDto {
    private UUID id;
    private String nombre;
    private String tipoNegocio;
    private String pais;
    private String estadoRegion;
    private String monedaBase;
    private String rol;

    public NegocioSummaryDto() {}

    public NegocioSummaryDto(UUID id, String nombre, String tipoNegocio, String pais, String estadoRegion, String monedaBase, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.tipoNegocio = tipoNegocio;
        this.pais = pais;
        this.estadoRegion = estadoRegion;
        this.monedaBase = monedaBase;
        this.rol = rol;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstadoRegion() { return estadoRegion; }
    public void setEstadoRegion(String estadoRegion) { this.estadoRegion = estadoRegion; }

    public String getMonedaBase() { return monedaBase; }
    public void setMonedaBase(String monedaBase) { this.monedaBase = monedaBase; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
