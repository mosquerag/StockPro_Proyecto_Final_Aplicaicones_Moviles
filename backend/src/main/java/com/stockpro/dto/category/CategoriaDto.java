package com.stockpro.dto.category;

import com.stockpro.model.entity.Categoria;

import java.util.UUID;

public class CategoriaDto {
    private UUID id;
    private UUID negocioId;
    private String nombre;
    private String descripcion;
    private String colorHex;
    private Boolean activo;

    public CategoriaDto() {}

    public CategoriaDto(Categoria c) {
        this.id = c.getId();
        this.negocioId = c.getNegocio().getId();
        this.nombre = c.getNombre();
        this.descripcion = c.getDescripcion();
        this.colorHex = c.getColorHex();
        this.activo = c.getActivo();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getNegocioId() { return negocioId; }
    public void setNegocioId(UUID negocioId) { this.negocioId = negocioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
