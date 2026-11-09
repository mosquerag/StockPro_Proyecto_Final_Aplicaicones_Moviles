package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class RecursoNoEncontradoException extends StockProException {
    public RecursoNoEncontradoException(String mensaje) {
        super("RECURSO_NO_ENCONTRADO", mensaje, HttpStatus.NOT_FOUND);
    }

    public RecursoNoEncontradoException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.NOT_FOUND);
    }
}
