package com.stockpro.dto.report;

import java.math.BigDecimal;

public class ImpuestoCobradoDto {

    private String categoriaFiscal;
    private BigDecimal tarifaPorcentaje = BigDecimal.ZERO;
    private BigDecimal baseGravable = BigDecimal.ZERO;
    private BigDecimal montoCobrado = BigDecimal.ZERO;
    private String moneda;

    public ImpuestoCobradoDto() {}

    public ImpuestoCobradoDto(String categoriaFiscal, BigDecimal tarifaPorcentaje, BigDecimal baseGravable, BigDecimal montoCobrado, String moneda) {
        this.categoriaFiscal = categoriaFiscal;
        this.tarifaPorcentaje = tarifaPorcentaje;
        this.baseGravable = baseGravable;
        this.montoCobrado = montoCobrado;
        this.moneda = moneda;
    }

    public String getCategoriaFiscal() { return categoriaFiscal; }
    public void setCategoriaFiscal(String categoriaFiscal) { this.categoriaFiscal = categoriaFiscal; }

    public BigDecimal getTarifaPorcentaje() { return tarifaPorcentaje; }
    public void setTarifaPorcentaje(BigDecimal tarifaPorcentaje) { this.tarifaPorcentaje = tarifaPorcentaje; }

    public BigDecimal getBaseGravable() { return baseGravable; }
    public void setBaseGravable(BigDecimal baseGravable) { this.baseGravable = baseGravable; }

    public BigDecimal getMontoCobrado() { return montoCobrado; }
    public void setMontoCobrado(BigDecimal montoCobrado) { this.montoCobrado = montoCobrado; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}
