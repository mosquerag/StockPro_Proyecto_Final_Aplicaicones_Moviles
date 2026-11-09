package com.stockpro.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiradoException extends StockProException {
    public TokenExpiradoException(String mensaje) {
        super("TOKEN_EXPIRADO", mensaje, HttpStatus.UNAUTHORIZED);
    }
}
