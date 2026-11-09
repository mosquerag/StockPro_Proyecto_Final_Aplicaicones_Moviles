package com.stockpro.dto.currency;

import com.stockpro.model.entity.TasaCambio;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TasaCambioDto {
    private UUID id;
    private String monedaOrigen;
    private String monedaDestino;
    private BigDecimal tasa;
    private OffsetDateTime fechaActualizacion;
    private String fuente;
    private Boolean esManual;

    public TasaCambioDto() {}

    public TasaCambioDto(TasaCambio t) {
        this.id = t.getId();
        this.monedaOrigen = t.getMonedaOrigen();
        this.monedaDestino = t.getMonedaDestino();
        this.tasa = t.getTasa();
        this.fechaActualizacion = t.getFechaActualizacion();
        this.fuente = t.getFuente();
        this.esManual = t.getEsManual();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMonedaOrigen() { return monedaOrigen; }
    public void setMonedaOrigen(String monedaOrigen) { this.monedaOrigen = monedaOrigen; }
    public String getMonedaDestino() { return monedaDestino; }
    public void setMonedaDestino(String monedaDestino) { this.monedaDestino = monedaDestino; }
    public BigDecimal getTasa() { return tasa; }
    public void setTasa(BigDecimal tasa) { this.tasa = tasa; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }
    public Boolean getEsManual() { return esManual; }
    public void setEsManual(Boolean esManual) { this.esManual = esManual; }
}
