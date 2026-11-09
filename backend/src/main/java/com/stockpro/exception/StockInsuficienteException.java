package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class StockInsuficienteException extends StockProException {
    public StockInsuficienteException(String mensaje) {
        super("STOCK_INSUFICIENTE", mensaje, HttpStatus.BAD_REQUEST);
    }
}
