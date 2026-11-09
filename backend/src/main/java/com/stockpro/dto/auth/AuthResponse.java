package com.stockpro.dto.auth;

import java.util.List;
import java.util.UUID;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private UUID usuarioId;
    private String nombre;
    private String correo;
    private NegocioSummaryDto negocioActivo;
    private List<NegocioSummaryDto> negocios;

    public AuthResponse() {}

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresInMs() { return expiresInMs; }
    public void setExpiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public NegocioSummaryDto getNegocioActivo() { return negocioActivo; }
    public void setNegocioActivo(NegocioSummaryDto negocioActivo) { this.negocioActivo = negocioActivo; }

    public List<NegocioSummaryDto> getNegocios() { return negocios; }
    public void setNegocios(List<NegocioSummaryDto> negocios) { this.negocios = negocios; }
}
