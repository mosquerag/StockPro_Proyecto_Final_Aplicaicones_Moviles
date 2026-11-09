package com.stockpro.dto.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class CreateEntradaPrevistaRequest {

    @NotNull(message = "La fecha prevista es obligatoria (hoy o mañana)")
    private LocalDate fechaPrevista;

    // Campo opcional de texto para referencia del origen de la mercancía sin tabla de proveedores
    private String origen;

    private String notas;

    @NotEmpty(message = "La entrada prevista debe contener al menos un producto")
    @Valid
    private List<CreateDetalleEntradaRequest> items;

    public CreateEntradaPrevistaRequest() {}

    public LocalDate getFechaPrevista() { return fechaPrevista; }
    public void setFechaPrevista(LocalDate fechaPrevista) { this.fechaPrevista = fechaPrevista; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public List<CreateDetalleEntradaRequest> getItems() { return items; }
    public void setItems(List<CreateDetalleEntradaRequest> items) { this.items = items; }
}
