package com.stockpro.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class SecurityUtils {

    private final JwtService jwtService;

    public SecurityUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public UUID getCurrentNegocioId() {
        String token = extractTokenFromCurrentRequest();
        if (token == null) {
            throw new IllegalStateException("No se encontró token de autorización en la petición");
        }
        UUID negocioId = jwtService.extractNegocioId(token);
        if (negocioId == null) {
            throw new IllegalStateException("El token no contiene un negocio_id asociado");
        }
        return negocioId;
    }

    public UUID getCurrentUsuarioId() {
        String token = extractTokenFromCurrentRequest();
        if (token == null) {
            throw new IllegalStateException("No se encontró token de autorización en la petición");
        }
        UUID usuarioId = jwtService.extractUserId(token);
        if (usuarioId == null) {
            throw new IllegalStateException("El token no contiene un usuario_id asociado");
        }
        return usuarioId;
    }

    public String getCurrentCorreo() {
        String token = extractTokenFromCurrentRequest();
        return token != null ? jwtService.extractUsername(token) : null;
    }

    public String getCurrentRol() {
        String token = extractTokenFromCurrentRequest();
        return token != null ? jwtService.extractRole(token) : null;
    }

    private String extractTokenFromCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
