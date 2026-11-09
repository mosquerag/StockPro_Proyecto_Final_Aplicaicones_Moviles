package com.stockpro.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public class CreateVentaRequest {

    @NotEmpty(message = "La venta debe contener al menos un producto")
    @Valid
    private List<CreateVentaItemRequest> items;

    private String monedaVenta; // 'COP', 'USD', 'EUR'. Si es nulo, usa la moneda base del negocio

    private BigDecimal tasaCambioManual; // Tasa de cambio manual opcional si el usuario desea fijarla

    private String metodoPago = "EFECTIVO"; // EFECTIVO, TARJETA, TRANSFERENCIA, MIXTO, OTRO

    private String notas;

    public CreateVentaRequest() {}

    public List<CreateVentaItemRequest> getItems() { return items; }
    public void setItems(List<CreateVentaItemRequest> items) { this.items = items; }

    public String getMonedaVenta() { return monedaVenta; }
    public void setMonedaVenta(String monedaVenta) { this.monedaVenta = monedaVenta; }

    public BigDecimal getTasaCambioManual() { return tasaCambioManual; }
    public void setTasaCambioManual(BigDecimal tasaCambioManual) { this.tasaCambioManual = tasaCambioManual; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
