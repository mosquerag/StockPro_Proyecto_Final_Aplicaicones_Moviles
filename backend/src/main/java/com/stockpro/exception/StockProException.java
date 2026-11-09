package com.stockpro.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción base del dominio StockPro con código de error unívoco y HttpStatus asociado.
 */
public class StockProException extends RuntimeException {

    private final String codigo;
    private final HttpStatus status;

    public StockProException(String codigo, String mensaje, HttpStatus status) {
        super(mensaje);
        this.codigo = codigo;
        this.status = status;
    }

    public StockProException(String codigo, String mensaje, HttpStatus status, Throwable cause) {
        super(mensaje, cause);
        this.codigo = codigo;
        this.status = status;
    }

    public String getCodigo() {
        return codigo;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
