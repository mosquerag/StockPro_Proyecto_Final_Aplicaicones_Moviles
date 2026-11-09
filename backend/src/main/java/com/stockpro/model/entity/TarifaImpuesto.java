package com.stockpro.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tarifas_impuesto")
public class TarifaImpuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 2)
    private String pais;

    @Column(name = "estado_region", length = 50)
    private String estadoRegion;

    @Column(name = "categoria_fiscal", nullable = false, length = 50)
    private String categoriaFiscal;

    @Column(name = "tipo_impuesto", nullable = false, length = 30)
    private String tipoImpuesto; // IVA, CONSUMO, SALES_TAX

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "vigencia_anio", nullable = false)
    private Integer vigenciaAnio = 2026;

    @Column(nullable = false, length = 255)
    private String fuente;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public TarifaImpuesto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstadoRegion() { return estadoRegion; }
    public void setEstadoRegion(String estadoRegion) { this.estadoRegion = estadoRegion; }

    public String getCategoriaFiscal() { return categoriaFiscal; }
    public void setCategoriaFiscal(String categoriaFiscal) { this.categoriaFiscal = categoriaFiscal; }

    public String getTipoImpuesto() { return tipoImpuesto; }
    public void setTipoImpuesto(String tipoImpuesto) { this.tipoImpuesto = tipoImpuesto; }

    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }

    public Integer getVigenciaAnio() { return vigenciaAnio; }
    public void setVigenciaAnio(Integer vigenciaAnio) { this.vigenciaAnio = vigenciaAnio; }

    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
