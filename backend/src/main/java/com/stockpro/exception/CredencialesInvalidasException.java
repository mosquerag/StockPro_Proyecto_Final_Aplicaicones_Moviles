package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class CredencialesInvalidasException extends StockProException {
    public CredencialesInvalidasException(String mensaje) {
        super("CREDENCIALES_INVALIDAS", mensaje, HttpStatus.UNAUTHORIZED);
    }
}
