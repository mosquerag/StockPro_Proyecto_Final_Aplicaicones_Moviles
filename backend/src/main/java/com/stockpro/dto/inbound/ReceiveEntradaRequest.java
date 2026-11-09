package com.stockpro.dto.inbound;

import jakarta.validation.Valid;

import java.util.List;

public class ReceiveEntradaRequest {

    // Lista de ítems recibidos con sus cantidades y vencimientos específicos.
    // Si la lista está vacía o es nula, el sistema recibe el 100% de las cantidades esperadas.
    @Valid
    private List<ReceiveItemRequest> items;

    private String notas;

    public ReceiveEntradaRequest() {}

    public List<ReceiveItemRequest> getItems() { return items; }
    public void setItems(List<ReceiveItemRequest> items) { this.items = items; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
