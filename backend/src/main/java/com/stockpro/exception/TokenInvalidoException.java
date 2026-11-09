package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class TokenInvalidoException extends StockProException {
    public TokenInvalidoException(String mensaje) {
        super("TOKEN_INVALIDO", mensaje, HttpStatus.UNAUTHORIZED);
    }
}
