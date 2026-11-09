package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class ValorDuplicadoException extends StockProException {
    public ValorDuplicadoException(String mensaje) {
        super("VALOR_DUPLICADO", mensaje, HttpStatus.CONFLICT);
    }

    public ValorDuplicadoException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.CONFLICT);
    }
}
