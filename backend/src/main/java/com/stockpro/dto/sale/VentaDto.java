package com.stockpro.dto.sale;

import com.stockpro.model.entity.Venta;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VentaDto {
    private UUID id;
    private UUID negocioId;
    private UUID usuarioId;
    private String usuarioNombre;
    private String numeroComprobante;
    private OffsetDateTime fechaVenta;
    private String monedaVenta;
    private BigDecimal tasaCambioBase;
    private BigDecimal subtotalBase;
    private BigDecimal totalImpuestos;
    private BigDecimal totalDescuentos;
    private BigDecimal totalVenta;
    private String metodoPago;
    private String estado;
    private String notas;
    private List<DetalleVentaDto> items;

    public VentaDto() {}

    public VentaDto(Venta v) {
        this.id = v.getId();
        this.negocioId = v.getNegocio().getId();
        if (v.getUsuario() != null) {
            this.usuarioId = v.getUsuario().getId();
            this.usuarioNombre = v.getUsuario().getNombre();
        }
        this.numeroComprobante = v.getNumeroComprobante();
        this.fechaVenta = v.getFechaVenta();
        this.monedaVenta = v.getMonedaVenta();
        this.tasaCambioBase = v.getTasaCambioBase();
        this.subtotalBase = v.getSubtotalBase();
        this.totalImpuestos = v.getTotalImpuestos();
        this.totalDescuentos = v.getTotalDescuentos();
        this.totalVenta = v.getTotalVenta();
        this.metodoPago = v.getMetodoPago();
        this.estado = v.getEstado();
        this.notas = v.getNotas();
        if (v.getDetalles() != null) {
            this.items = v.getDetalles().stream().map(DetalleVentaDto::new).collect(Collectors.toList());
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getNegocioId() { return negocioId; }
    public void setNegocioId(UUID negocioId) { this.negocioId = negocioId; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    public OffsetDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(OffsetDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    public String getMonedaVenta() { return monedaVenta; }
    public void setMonedaVenta(String monedaVenta) { this.monedaVenta = monedaVenta; }
    public BigDecimal getTasaCambioBase() { return tasaCambioBase; }
    public void setTasaCambioBase(BigDecimal tasaCambioBase) { this.tasaCambioBase = tasaCambioBase; }
    public BigDecimal getSubtotalBase() { return subtotalBase; }
    public void setSubtotalBase(BigDecimal subtotalBase) { this.subtotalBase = subtotalBase; }
    public BigDecimal getTotalImpuestos() { return totalImpuestos; }
    public void setTotalImpuestos(BigDecimal totalImpuestos) { this.totalImpuestos = totalImpuestos; }
    public BigDecimal getTotalDescuentos() { return totalDescuentos; }
    public void setTotalDescuentos(BigDecimal totalDescuentos) { this.totalDescuentos = totalDescuentos; }
    public BigDecimal getTotalVenta() { return totalVenta; }
    public void setTotalVenta(BigDecimal totalVenta) { this.totalVenta = totalVenta; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public List<DetalleVentaDto> getItems() { return items; }
    public void setItems(List<DetalleVentaDto> items) { this.items = items; }
}
