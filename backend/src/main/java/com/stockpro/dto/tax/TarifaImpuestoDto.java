package com.stockpro.dto.tax;

import com.stockpro.model.entity.TarifaImpuesto;
import java.math.BigDecimal;
import java.util.UUID;

public class TarifaImpuestoDto {
    private UUID id;
    private String pais;
    private String estadoRegion;
    private String categoriaFiscal;
    private String tipoImpuesto;
    private BigDecimal porcentaje;
    private Integer vigenciaAnio;
    private String fuente;
    private String descripcion;

    public TarifaImpuestoDto() {}

    public TarifaImpuestoDto(TarifaImpuesto t) {
        this.id = t.getId();
        this.pais = t.getPais();
        this.estadoRegion = t.getEstadoRegion();
        this.categoriaFiscal = t.getCategoriaFiscal();
        this.tipoImpuesto = t.getTipoImpuesto();
        this.porcentaje = t.getPorcentaje();
        this.vigenciaAnio = t.getVigenciaAnio();
        this.fuente = t.getFuente();
        this.descripcion = t.getDescripcion();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstadoRegion() { return estadoRegion; }
    public void setEstadoRegion(String estadoRegion) { this.estadoRegion = estadoRegion; }

    public String getCategoriaFiscal() { return categoriaFiscal; }
    public void setCategoriaFiscal(String categoriaFiscal) { this.categoriaFiscal = categoriaFiscal; }

    public String getTipoImpuesto() { return tipoImpuesto; }
    public void setTipoImpuesto(String tipoImpuesto) { this.tipoImpuesto = tipoImpuesto; }

    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }

    public Integer getVigenciaAnio() { return vigenciaAnio; }
    public void setVigenciaAnio(Integer vigenciaAnio) { this.vigenciaAnio = vigenciaAnio; }

    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
