package com.stockpro.dto.report;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteGeneralDto {

    private String periodo; // "7D", "30D", "3M", "CUSTOM"
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFin;
    private String monedaReporte;

    private GananciasReportDto ganancias;
    private List<ProductoRankingDto> masVendidos = new ArrayList<>();
    private List<ProductoRankingDto> menosVendidos = new ArrayList<>();
    private PerdidasReportDto perdidasYBajas;
    private List<ProductividadProductoDto> productividadProductos = new ArrayList<>();
    private List<ImpuestoCobradoDto> impuestosCobrados = new ArrayList<>();

    public ReporteGeneralDto() {}

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public OffsetDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(OffsetDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public OffsetDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(OffsetDateTime fechaFin) { this.fechaFin = fechaFin; }

    public String getMonedaReporte() { return monedaReporte; }
    public void setMonedaReporte(String monedaReporte) { this.monedaReporte = monedaReporte; }

    public GananciasReportDto getGanancias() { return ganancias; }
    public void setGanancias(GananciasReportDto ganancias) { this.ganancias = ganancias; }

    public List<ProductoRankingDto> getMasVendidos() { return masVendidos; }
    public void setMasVendidos(List<ProductoRankingDto> masVendidos) { this.masVendidos = masVendidos; }

    public List<ProductoRankingDto> getMenosVendidos() { return menosVendidos; }
    public void setMenosVendidos(List<ProductoRankingDto> menosVendidos) { this.menosVendidos = menosVendidos; }

    public PerdidasReportDto getPerdidasYBajas() { return perdidasYBajas; }
    public void setPerdidasYBajas(PerdidasReportDto perdidasYBajas) { this.perdidasYBajas = perdidasYBajas; }

    public List<ProductividadProductoDto> getProductividadProductos() { return productividadProductos; }
    public void setProductividadProductos(List<ProductividadProductoDto> productividadProductos) { this.productividadProductos = productividadProductos; }

    public List<ImpuestoCobradoDto> getImpuestosCobrados() { return impuestosCobrados; }
    public void setImpuestosCobrados(List<ImpuestoCobradoDto> impuestosCobrados) { this.impuestosCobrados = impuestosCobrados; }
}
