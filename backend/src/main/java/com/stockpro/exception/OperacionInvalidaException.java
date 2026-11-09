package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class OperacionInvalidaException extends StockProException {
    public OperacionInvalidaException(String mensaje) {
        super("OPERACION_INVALIDA", mensaje, HttpStatus.BAD_REQUEST);
    }

    public OperacionInvalidaException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.BAD_REQUEST);
    }
}
