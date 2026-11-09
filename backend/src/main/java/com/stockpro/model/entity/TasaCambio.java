package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasas_cambio", uniqueConstraints = {
    @UniqueConstraint(name = "uq_par_monedas", columnNames = {"moneda_origen", "moneda_destino"})
})
public class TasaCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "moneda_origen", nullable = false, length = 3)
    private String monedaOrigen; // COP, USD, EUR

    @Column(name = "moneda_destino", nullable = false, length = 3)
    private String monedaDestino; // COP, USD, EUR

    @Column(nullable = false, precision = 16, scale = 6)
    private BigDecimal tasa;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion = OffsetDateTime.now();

    @Column(nullable = false, length = 100)
    private String fuente = "ExchangeRate-API";

    @Column(name = "es_manual", nullable = false)
    private Boolean esManual = false;

    public TasaCambio() {}

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
