package com.stockpro.exception;

import com.stockpro.dto.error.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de excepciones global de Spring Boot.
 * Garantiza que cualquier error o fallo de validación devuelva un formato unificado
 * {@link ApiErrorResponse} con un código unívoco y estable para internacionalización en Android.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StockProException.class)
    public ResponseEntity<ApiErrorResponse> handleStockProException(StockProException ex) {
        log.warn("Excepción de dominio capturada [{}]: {}", ex.getCodigo(), ex.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                ex.getCodigo(),
                ex.getMessage(),
                ex.getStatus().value()
        );
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Fallo de credenciales: {}", ex.getMessage());
        String codigo = "CREDENCIALES_INVALIDAS";
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("expirado")) {
            codigo = "TOKEN_EXPIRADO";
        } else if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("inválido")) {
            codigo = "TOKEN_INVALIDO";
        }
        ApiErrorResponse response = new ApiErrorResponse(
                codigo,
                ex.getMessage() != null ? ex.getMessage() : "Credenciales inválidas",
                HttpStatus.UNAUTHORIZED.value()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                "ACCESO_DENEGADO",
                "No tienes permisos para realizar esta acción.",
                HttpStatus.FORBIDDEN.value()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiErrorResponse response = new ApiErrorResponse(
                "VALIDACION_FALLIDA",
                "Los datos de entrada son inválidos o faltan campos obligatorios",
                HttpStatus.BAD_REQUEST.value(),
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        String msg = ex.getMessage() != null ? ex.getMessage() : "Parámetro inválido";
        String lower = msg.toLowerCase();

        String codigo = "SOLICITUD_INVALIDA";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (lower.contains("stock insuficiente") || lower.contains("insuficiente")) {
            codigo = "STOCK_INSUFICIENTE";
        } else if (lower.contains("no encontrado") || lower.contains("no encontrada") || lower.contains("no pertenece")) {
            if (lower.contains("producto")) {
                codigo = "PRODUCTO_NO_ENCONTRADO";
            } else if (lower.contains("lote")) {
                codigo = "LOTE_NO_ENCONTRADO";
            } else if (lower.contains("negocio")) {
                codigo = "NEGOCIO_NO_ENCONTRADO";
            } else if (lower.contains("entrada")) {
                codigo = "ENTRADA_NO_ENCONTRADA";
            } else if (lower.contains("venta")) {
                codigo = "VENTA_NO_ENCONTRADA";
            } else {
                codigo = "RECURSO_NO_ENCONTRADO";
            }
            status = HttpStatus.NOT_FOUND;
        } else if (lower.contains("existe") || lower.contains("registrada") || lower.contains("correo")) {
            codigo = "EMAIL_DUPLICADO";
            status = HttpStatus.CONFLICT;
        }

        ApiErrorResponse response = new ApiErrorResponse(codigo, msg, status.value());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("IllegalStateException: {}", ex.getMessage());
        String msg = ex.getMessage() != null ? ex.getMessage() : "Estado inválido";
        String lower = msg.toLowerCase();

        String codigo = "OPERACION_INVALIDA";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (lower.contains("stock insuficiente") || lower.contains("insuficiente")) {
            codigo = "STOCK_INSUFICIENTE";
        } else if (lower.contains("inactivo")) {
            codigo = "PRODUCTO_INACTIVO";
        } else if (lower.contains("recibida") || lower.contains("estado")) {
            codigo = "ENTRADA_YA_RECIBIDA";
        }

        ApiErrorResponse response = new ApiErrorResponse(codigo, msg, status.value());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        log.error("Error no controlado en el servidor: ", ex);
        ApiErrorResponse response = new ApiErrorResponse(
                "ERROR_INTERNO",
                "Ocurrió un error inesperado en el servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
