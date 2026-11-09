package com.stockpro.dto.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PerdidasReportDto {

    private BigDecimal totalUnidadesPerdidas = BigDecimal.ZERO;
    private BigDecimal costoTotalPerdidas = BigDecimal.ZERO;
    private String moneda;
    private List<PerdidaItemDto> detalles = new ArrayList<>();

    public PerdidasReportDto() {}

    public BigDecimal getTotalUnidadesPerdidas() { return totalUnidadesPerdidas; }
    public void setTotalUnidadesPerdidas(BigDecimal totalUnidadesPerdidas) { this.totalUnidadesPerdidas = totalUnidadesPerdidas; }

    public BigDecimal getCostoTotalPerdidas() { return costoTotalPerdidas; }
    public void setCostoTotalPerdidas(BigDecimal costoTotalPerdidas) { this.costoTotalPerdidas = costoTotalPerdidas; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public List<PerdidaItemDto> getDetalles() { return detalles; }
    public void setDetalles(List<PerdidaItemDto> detalles) { this.detalles = detalles; }
}
