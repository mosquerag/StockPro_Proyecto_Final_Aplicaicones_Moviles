package com.stockpro.dto.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateNegocioRequest {

    @NotBlank(message = "El nombre del negocio es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    @NotBlank(message = "El país es obligatorio")
    @Size(min = 2, max = 2, message = "El código de país debe ser ISO 2 caracteres (CO, US, ES)")
    private String pais;

    @Size(max = 100, message = "El estado o región no puede exceder 100 caracteres")
    private String estadoRegion;

    @NotBlank(message = "La moneda base es obligatoria")
    @Size(min = 3, max = 3, message = "La moneda base debe tener 3 caracteres (COP, USD, EUR)")
    private String monedaBase;

    private String tipoNegocio;
    private String identificacionFiscal;
    private Boolean permiteStockNegativo;

    public UpdateNegocioRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstadoRegion() { return estadoRegion; }
    public void setEstadoRegion(String estadoRegion) { this.estadoRegion = estadoRegion; }

    public String getMonedaBase() { return monedaBase; }
    public void setMonedaBase(String monedaBase) { this.monedaBase = monedaBase; }

    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }

    public String getIdentificacionFiscal() { return identificacionFiscal; }
    public void setIdentificacionFiscal(String identificacionFiscal) { this.identificacionFiscal = identificacionFiscal; }

    public Boolean getPermiteStockNegativo() { return permiteStockNegativo; }
    public void setPermiteStockNegativo(Boolean permiteStockNegativo) { this.permiteStockNegativo = permiteStockNegativo; }
}
