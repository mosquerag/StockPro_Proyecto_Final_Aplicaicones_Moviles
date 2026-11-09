package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "detalle_entradas_previstas")
public class DetalleEntradaPrevista {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_prevista_id", nullable = false)
    private EntradaPrevista entradaPrevista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_esperada", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadEsperada;

    @Column(name = "cantidad_recibida", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadRecibida = BigDecimal.ZERO;

    @Column(name = "costo_unitario", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoUnitario = BigDecimal.ZERO;

    public DetalleEntradaPrevista() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public EntradaPrevista getEntradaPrevista() { return entradaPrevista; }
    public void setEntradaPrevista(EntradaPrevista entradaPrevista) { this.entradaPrevista = entradaPrevista; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public BigDecimal getCantidadEsperada() { return cantidadEsperada; }
    public void setCantidadEsperada(BigDecimal cantidadEsperada) { this.cantidadEsperada = cantidadEsperada; }

    public BigDecimal getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(BigDecimal cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
}
