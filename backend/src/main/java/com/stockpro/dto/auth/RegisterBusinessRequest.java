package com.stockpro.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterBusinessRequest {

    // Datos del Propietario
    @NotBlank(message = "El nombre del propietario es obligatorio")
    private String nombrePropietario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    private String telefono;

    // Datos del Negocio
    @NotBlank(message = "El nombre del negocio es obligatorio")
    private String nombreNegocio;

    @NotBlank(message = "El tipo de negocio es obligatorio")
    private String tipoNegocio; // TIENDA, RESTAURANTE, SUPERMERCADO, ROPA, etc.

    private String identificacionFiscal; // NIT, EIN, etc.

    @NotBlank(message = "El país es obligatorio")
    @Size(min = 2, max = 2, message = "El código de país debe ser ISO 2 (ej. CO, US, IT)")
    private String pais;

    private String estadoRegion; // CA, TX, Antioquia, etc.

    @NotBlank(message = "La moneda base es obligatoria")
    private String monedaBase; // COP, USD, EUR

    public RegisterBusinessRequest() {}

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNombreNegocio() { return nombreNegocio; }
    public void setNombreNegocio(String nombreNegocio) { this.nombreNegocio = nombreNegocio; }

    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }

    public String getIdentificacionFiscal() { return identificacionFiscal; }
    public void setIdentificacionFiscal(String identificacionFiscal) { this.identificacionFiscal = identificacionFiscal; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstadoRegion() { return estadoRegion; }
    public void setEstadoRegion(String estadoRegion) { this.estadoRegion = estadoRegion; }

    public String getMonedaBase() { return monedaBase; }
    public void setMonedaBase(String monedaBase) { this.monedaBase = monedaBase; }
}
