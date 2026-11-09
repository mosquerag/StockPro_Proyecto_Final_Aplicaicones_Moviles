package com.stockpro.dto.report;

import java.math.BigDecimal;

public class GananciasReportDto {

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalCostos = BigDecimal.ZERO;
    private BigDecimal gananciaBruta = BigDecimal.ZERO;
    private BigDecimal margenPorcentual = BigDecimal.ZERO;
    private String moneda;

    public GananciasReportDto() {}

    public BigDecimal getTotalVentas() { return totalVentas; }
    public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

    public BigDecimal getTotalCostos() { return totalCostos; }
    public void setTotalCostos(BigDecimal totalCostos) { this.totalCostos = totalCostos; }

    public BigDecimal getGananciaBruta() { return gananciaBruta; }
    public void setGananciaBruta(BigDecimal gananciaBruta) { this.gananciaBruta = gananciaBruta; }

    public BigDecimal getMargenPorcentual() { return margenPorcentual; }
    public void setMargenPorcentual(BigDecimal margenPorcentual) { this.margenPorcentual = margenPorcentual; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}
