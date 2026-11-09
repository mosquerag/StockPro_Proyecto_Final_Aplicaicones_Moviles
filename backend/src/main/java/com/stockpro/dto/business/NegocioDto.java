package com.stockpro.dto.business;

import com.stockpro.model.entity.Negocio;
import java.util.UUID;

public class NegocioDto {
    private UUID id;
    private String nombre;
    private String tipoNegocio;
    private String identificacionFiscal;
    private String pais;
    private String estadoRegion;
    private String monedaBase;
    private String zonaHoraria;
    private Boolean permiteStockNegativo;

    public NegocioDto() {}

    public NegocioDto(Negocio n) {
        this.id = n.getId();
        this.nombre = n.getNombre();
        this.tipoNegocio = n.getTipoNegocio();
        this.identificacionFiscal = n.getIdentificacionFiscal();
        this.pais = n.getPais();
        this.estadoRegion = n.getEstadoRegion();
        this.monedaBase = n.getMonedaBase();
        this.zonaHoraria = n.getZonaHoraria();
        this.permiteStockNegativo = n.getPermiteStockNegativo();
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
}
