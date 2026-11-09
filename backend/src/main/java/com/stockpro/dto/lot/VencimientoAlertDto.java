package com.stockpro.dto.lot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class VencimientoAlertDto {
    private UUID loteId;
    private UUID productoId;
    private String productoNombre;
    private String categoriaNombre;
    private String numeroLote;
    private BigDecimal cantidadDisponible;
    private LocalDate fechaVencimiento;
    private long diasRestantes; // Negativo si ya venció
    private String estadoAlerta; // 'VENCIDO' o 'PROXIMO_VENCER'

    public VencimientoAlertDto() {}

    public VencimientoAlertDto(UUID loteId, UUID productoId, String productoNombre, String categoriaNombre, String numeroLote, BigDecimal cantidadDisponible, LocalDate fechaVencimiento, long diasRestantes, String estadoAlerta) {
        this.loteId = loteId;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.categoriaNombre = categoriaNombre;
        this.numeroLote = numeroLote;
        this.cantidadDisponible = cantidadDisponible;
        this.fechaVencimiento = fechaVencimiento;
        this.diasRestantes = diasRestantes;
        this.estadoAlerta = estadoAlerta;
    }

    public UUID getLoteId() { return loteId; }
    public void setLoteId(UUID loteId) { this.loteId = loteId; }
    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public BigDecimal getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(BigDecimal cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(long diasRestantes) { this.diasRestantes = diasRestantes; }
    public String getEstadoAlerta() { return estadoAlerta; }
    public void setEstadoAlerta(String estadoAlerta) { this.estadoAlerta = estadoAlerta; }
}
