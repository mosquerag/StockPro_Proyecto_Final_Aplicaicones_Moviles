package com.stockpro.dto.report;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductoRankingDto {

    private UUID productoId;
    private String productoNombre;
    private String categoriaNombre;
    private BigDecimal cantidadVendida = BigDecimal.ZERO;
    private BigDecimal totalRecaudado = BigDecimal.ZERO;
    private BigDecimal gananciaAportada = BigDecimal.ZERO;

    public ProductoRankingDto() {}

    public ProductoRankingDto(UUID productoId, String productoNombre, String categoriaNombre,
                              BigDecimal cantidadVendida, BigDecimal totalRecaudado, BigDecimal gananciaAportada) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.categoriaNombre = categoriaNombre;
        this.cantidadVendida = cantidadVendida;
        this.totalRecaudado = totalRecaudado;
        this.gananciaAportada = gananciaAportada;
    }

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public BigDecimal getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(BigDecimal cantidadVendida) { this.cantidadVendida = cantidadVendida; }

    public BigDecimal getTotalRecaudado() { return totalRecaudado; }
    public void setTotalRecaudado(BigDecimal totalRecaudado) { this.totalRecaudado = totalRecaudado; }

    public BigDecimal getGananciaAportada() { return gananciaAportada; }
    public void setGananciaAportada(BigDecimal gananciaAportada) { this.gananciaAportada = gananciaAportada; }
}
