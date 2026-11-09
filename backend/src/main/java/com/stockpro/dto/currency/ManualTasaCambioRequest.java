package com.stockpro.dto.currency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ManualTasaCambioRequest {

    @NotBlank(message = "La moneda origen es obligatoria (ej. USD)")
    private String monedaOrigen;

    @NotBlank(message = "La moneda destino es obligatoria (ej. COP)")
    private String monedaDestino;

    @NotNull(message = "La tasa es obligatoria")
    @DecimalMin(value = "0.000001", message = "La tasa debe ser mayor a cero")
    private BigDecimal tasa;

    public ManualTasaCambioRequest() {}

    public String getMonedaOrigen() { return monedaOrigen; }
    public void setMonedaOrigen(String monedaOrigen) { this.monedaOrigen = monedaOrigen; }

    public String getMonedaDestino() { return monedaDestino; }
    public void setMonedaDestino(String monedaDestino) { this.monedaDestino = monedaDestino; }

    public BigDecimal getTasa() { return tasa; }
    public void setTasa(BigDecimal tasa) { this.tasa = tasa; }
}
