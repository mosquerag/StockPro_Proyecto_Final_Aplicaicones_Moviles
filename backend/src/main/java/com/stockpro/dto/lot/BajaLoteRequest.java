package com.stockpro.dto.lot;

import jakarta.validation.constraints.NotBlank;

public class BajaLoteRequest {

    @NotBlank(message = "El motivo de la baja es obligatorio (ej. Vencimiento, Producto Dañado, Pérdida)")
    private String motivo = "Baja por fecha de vencimiento expirada";

    public BajaLoteRequest() {}

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
