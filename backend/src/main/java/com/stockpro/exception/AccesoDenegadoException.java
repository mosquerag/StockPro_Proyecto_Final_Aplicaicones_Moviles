package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class AccesoDenegadoException extends StockProException {
    public AccesoDenegadoException(String mensaje) {
        super("ACCESO_DENEGADO", mensaje, HttpStatus.FORBIDDEN);
    }
}
