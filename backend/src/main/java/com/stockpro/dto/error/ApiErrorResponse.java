package com.stockpro.dto.error;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Estructura estandarizada de error devuelta por la API REST de StockPro.
 * Permite que los clientes móviles (Android) identifiquen el error mediante
 * un código estable unívoco y traduzcan el mensaje según el idioma activo.
 */
public class ApiErrorResponse {

    private String codigo;
    private String mensaje;
    private int status;
    private OffsetDateTime timestamp;
    private Map<String, String> detalles;

    public ApiErrorResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    public ApiErrorResponse(String codigo, String mensaje, int status) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.status = status;
        this.timestamp = OffsetDateTime.now();
    }

    public ApiErrorResponse(String codigo, String mensaje, int status, Map<String, String> detalles) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.status = status;
        this.timestamp = OffsetDateTime.now();
        this.detalles = detalles;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getDetalles() {
        return detalles;
    }

    public void setDetalles(Map<String, String> detalles) {
        this.detalles = detalles;
    }
}
