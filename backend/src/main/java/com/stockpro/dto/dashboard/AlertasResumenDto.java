package com.stockpro.dto.dashboard;

import com.stockpro.dto.lot.VencimientoAlertDto;
import com.stockpro.dto.product.ProductoDto;

import java.util.ArrayList;
import java.util.List;

public class AlertasResumenDto {

    private int totalAlertas;
    private int stockBajoCount;
    private int porVencerCount;
    private int vencidosCount;

    private List<ProductoDto> stockBajo = new ArrayList<>();
    private List<VencimientoAlertDto> porVencer = new ArrayList<>();
    private List<VencimientoAlertDto> vencidos = new ArrayList<>();

    public AlertasResumenDto() {}

    public int getTotalAlertas() { return totalAlertas; }
    public void setTotalAlertas(int totalAlertas) { this.totalAlertas = totalAlertas; }

    public int getStockBajoCount() { return stockBajoCount; }
    public void setStockBajoCount(int stockBajoCount) { this.stockBajoCount = stockBajoCount; }

    public int getPorVencerCount() { return porVencerCount; }
    public void setPorVencerCount(int porVencerCount) { this.porVencerCount = porVencerCount; }

    public int getVencidosCount() { return vencidosCount; }
    public void setVencidosCount(int vencidosCount) { this.vencidosCount = vencidosCount; }

    public List<ProductoDto> getStockBajo() { return stockBajo; }
    public void setStockBajo(List<ProductoDto> stockBajo) { this.stockBajo = stockBajo; }

    public List<VencimientoAlertDto> getPorVencer() { return porVencer; }
    public void setPorVencer(List<VencimientoAlertDto> porVencer) { this.porVencer = porVencer; }

    public List<VencimientoAlertDto> getVencidos() { return vencidos; }
    public void setVencidos(List<VencimientoAlertDto> vencidos) { this.vencidos = vencidos; }
}
