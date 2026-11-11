package com.stockpro.dto.dashboard;

import com.stockpro.dto.inbound.EntradaPrevistaDto;
import com.stockpro.dto.sale.VentaDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardDto {

    private String negocioNombre;
    private String moneda;
    private BigDecimal ventasHoy;
    private BigDecimal gananciasEstimadasHoy;
    private int cantidadVentasHoy;
    private long cantidadProductos;
    private AlertasResumenDto alertas;
    private List<VentaDto> ultimasVentas = new ArrayList<>();
    private List<EntradaPrevistaDto> entradasHoy = new ArrayList<>();
    private List<EntradaPrevistaDto> entradasManana = new ArrayList<>();

    public DashboardDto() {}

    public String getNegocioNombre() { return negocioNombre; }
    public void setNegocioNombre(String negocioNombre) { this.negocioNombre = negocioNombre; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getVentasHoy() { return ventasHoy; }
    public void setVentasHoy(BigDecimal ventasHoy) { this.ventasHoy = ventasHoy; }

    public BigDecimal getGananciasEstimadasHoy() { return gananciasEstimadasHoy; }
    public void setGananciasEstimadasHoy(BigDecimal gananciasEstimadasHoy) { this.gananciasEstimadasHoy = gananciasEstimadasHoy; }

    public int getCantidadVentasHoy() { return cantidadVentasHoy; }
    public void setCantidadVentasHoy(int cantidadVentasHoy) { this.cantidadVentasHoy = cantidadVentasHoy; }

    public long getCantidadProductos() { return cantidadProductos; }
    public void setCantidadProductos(long cantidadProductos) { this.cantidadProductos = cantidadProductos; }

    public AlertasResumenDto getAlertas() { return alertas; }
    public void setAlertas(AlertasResumenDto alertas) { this.alertas = alertas; }

    public List<VentaDto> getUltimasVentas() { return ultimasVentas; }
    public void setUltimasVentas(List<VentaDto> ultimasVentas) { this.ultimasVentas = ultimasVentas; }

    public List<EntradaPrevistaDto> getEntradasHoy() { return entradasHoy; }
    public void setEntradasHoy(List<EntradaPrevistaDto> entradasHoy) { this.entradasHoy = entradasHoy; }

    public List<EntradaPrevistaDto> getEntradasManana() { return entradasManana; }
    public void setEntradasManana(List<EntradaPrevistaDto> entradasManana) { this.entradasManana = entradasManana; }
}
