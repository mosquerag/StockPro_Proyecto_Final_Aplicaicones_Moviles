package com.stockpro.dto.report;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductividadProductoDto {

    private UUID productoId;
    private String productoNombre;
    private String categoriaNombre;
    private BigDecimal unidadesVendidas = BigDecimal.ZERO;
    private BigDecimal facturacionTotal = BigDecimal.ZERO;
    private BigDecimal costoTotal = BigDecimal.ZERO;
    private BigDecimal gananciaNeta = BigDecimal.ZERO;
    private BigDecimal margenPorcentual = BigDecimal.ZERO;

    public ProductividadProductoDto() {}

    public ProductividadProductoDto(UUID productoId, String productoNombre, String categoriaNombre,
                                    BigDecimal unidadesVendidas, BigDecimal facturacionTotal,
                                    BigDecimal costoTotal, BigDecimal gananciaNeta, BigDecimal margenPorcentual) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.categoriaNombre = categoriaNombre;
        this.unidadesVendidas = unidadesVendidas;
        this.facturacionTotal = facturacionTotal;
        this.costoTotal = costoTotal;
        this.gananciaNeta = gananciaNeta;
        this.margenPorcentual = margenPorcentual;
    }

    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public BigDecimal getUnidadesVendidas() { return unidadesVendidas; }
    public void setUnidadesVendidas(BigDecimal unidadesVendidas) { this.unidadesVendidas = unidadesVendidas; }

    public BigDecimal getFacturacionTotal() { return facturacionTotal; }
    public void setFacturacionTotal(BigDecimal facturacionTotal) { this.facturacionTotal = facturacionTotal; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public BigDecimal getGananciaNeta() { return gananciaNeta; }
    public void setGananciaNeta(BigDecimal gananciaNeta) { this.gananciaNeta = gananciaNeta; }

    public BigDecimal getMargenPorcentual() { return margenPorcentual; }
    public void setMargenPorcentual(BigDecimal margenPorcentual) { this.margenPorcentual = margenPorcentual; }
}
