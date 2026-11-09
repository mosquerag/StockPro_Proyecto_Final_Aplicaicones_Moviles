package com.stockpro.service;

import com.stockpro.dto.auth.*;
import com.stockpro.model.entity.*;
import com.stockpro.repository.*;
import com.stockpro.exception.*;
import com.stockpro.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final NegocioRepository negocioRepository;
    private final MiembroNegocioRepository miembroNegocioRepository;
    private final CategoriaRepository categoriaRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UsuarioRepository usuarioRepository,
            NegocioRepository negocioRepository,
            MiembroNegocioRepository miembroNegocioRepository,
            CategoriaRepository categoriaRepository,
            AuditoriaRepository auditoriaRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.usuarioRepository = usuarioRepository;
        this.negocioRepository = negocioRepository;
        this.miembroNegocioRepository = miembroNegocioRepository;
        this.categoriaRepository = categoriaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse registerBusiness(RegisterBusinessRequest request) {
        if (usuarioRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new ValorDuplicadoException("EMAIL_DUPLICADO", "Ya existe una cuenta registrada con el correo: " + request.getCorreo());
        }

        // 1. Crear y persistir el Usuario Propietario con hash BCrypt
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombrePropietario());
        usuario.setCorreo(request.getCorreo().trim().toLowerCase());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario = usuarioRepository.save(usuario);

        // 2. Crear y persistir el Negocio
        Negocio negocio = new Negocio();
        negocio.setNombre(request.getNombreNegocio());
        negocio.setTipoNegocio(request.getTipoNegocio().toUpperCase());
        negocio.setIdentificacionFiscal(request.getIdentificacionFiscal());
        negocio.setPais(request.getPais().toUpperCase());
        negocio.setEstadoRegion(request.getEstadoRegion());
        negocio.setMonedaBase(request.getMonedaBase().toUpperCase());

        // Zona horaria según país
        if ("US".equalsIgnoreCase(request.getPais())) {
            negocio.setZonaHoraria("America/Los_Angeles");
        } else if ("IT".equalsIgnoreCase(request.getPais()) || "ES".equalsIgnoreCase(request.getPais())) {
            negocio.setZonaHoraria("Europe/Rome");
        } else {
            negocio.setZonaHoraria("America/Bogota");
        }
        negocio = negocioRepository.save(negocio);

        // 3. Crear Membresía con rol PROPIETARIO
        MiembroNegocio miembro = new MiembroNegocio(negocio, usuario, "PROPIETARIO");
        miembroNegocioRepository.save(miembro);

        // 4. Crear Categorías base iniciales para el negocio
        crearCategoriasIniciales(negocio);

        // 5. Registrar en Auditoría
        Auditoria auditoria = new Auditoria(
                negocio,
                usuario,
                "REGISTRO_NEGOCIO",
                "NEGOCIO",
                negocio.getId(),
                "{\"negocio\": \"" + negocio.getNombre() + "\", \"pais\": \"" + negocio.getPais() + "\"}",
                "127.0.0.1"
        );
        auditoriaRepository.save(auditoria);

        // 6. Generar Tokens JWT
        String accessToken = jwtService.generateAccessToken(usuario.getId(), usuario.getCorreo(), negocio.getId(), "PROPIETARIO");
        String refreshToken = jwtService.generateRefreshToken(usuario.getId(), usuario.getCorreo());

        NegocioSummaryDto negocioDto = new NegocioSummaryDto(
                negocio.getId(),
                negocio.getNombre(),
                negocio.getTipoNegocio(),
                negocio.getPais(),
                negocio.getEstadoRegion(),
                negocio.getMonedaBase(),
                "PROPIETARIO"
        );

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresInMs(jwtService.getAccessTokenExpirationMs());
        response.setUsuarioId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setNegocioActivo(negocioDto);
        response.setNegocios(List.of(negocioDto));

        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo().trim().toLowerCase(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Correo o contraseña incorrectos");
        }

        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.getCorreo().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        List<MiembroNegocio> membresias = miembroNegocioRepository.findAllByUsuarioCorreoWithNegocio(usuario.getCorreo());

        if (membresias.isEmpty()) {
            throw new IllegalStateException("El usuario no tiene ningún negocio asociado");
        }

        List<NegocioSummaryDto> negociosDto = membresias.stream().map(m -> new NegocioSummaryDto(
                m.getNegocio().getId(),
                m.getNegocio().getNombre(),
                m.getNegocio().getTipoNegocio(),
                m.getNegocio().getPais(),
                m.getNegocio().getEstadoRegion(),
                m.getNegocio().getMonedaBase(),
                m.getRol()
        )).collect(Collectors.toList());

        // Negocio activo principal (el primero disponible)
        NegocioSummaryDto negocioActivo = negociosDto.get(0);

        String accessToken = jwtService.generateAccessToken(
                usuario.getId(),
                usuario.getCorreo(),
                negocioActivo.getId(),
                negocioActivo.getRol()
        );
        String refreshToken = jwtService.generateRefreshToken(usuario.getId(), usuario.getCorreo());

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresInMs(jwtService.getAccessTokenExpirationMs());
        response.setUsuarioId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setNegocioActivo(negocioActivo);
        response.setNegocios(negociosDto);

        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (jwtService.isTokenExpired(token)) {
            throw new TokenExpiradoException("El refresh token ha expirado. Inicia sesión nuevamente");
        }

        String tokenType = jwtService.extractTokenType(token);
        if (!"REFRESH".equals(tokenType)) {
            throw new TokenInvalidoException("Token inválido para renovación");
        }

        String correo = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        List<MiembroNegocio> membresias = miembroNegocioRepository.findAllByUsuarioCorreoWithNegocio(usuario.getCorreo());
        if (membresias.isEmpty()) {
            throw new IllegalStateException("Usuario sin negocios asignados");
        }

        List<NegocioSummaryDto> negociosDto = membresias.stream().map(m -> new NegocioSummaryDto(
                m.getNegocio().getId(),
                m.getNegocio().getNombre(),
                m.getNegocio().getTipoNegocio(),
                m.getNegocio().getPais(),
                m.getNegocio().getEstadoRegion(),
                m.getNegocio().getMonedaBase(),
                m.getRol()
        )).collect(Collectors.toList());

        NegocioSummaryDto negocioActivo = negociosDto.get(0);

        // Se generan nuevos tokens (rotación de refresh token)
        String newAccessToken = jwtService.generateAccessToken(
                usuario.getId(),
                usuario.getCorreo(),
                negocioActivo.getId(),
                negocioActivo.getRol()
        );
        String newRefreshToken = jwtService.generateRefreshToken(usuario.getId(), usuario.getCorreo());

        AuthResponse response = new AuthResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresInMs(jwtService.getAccessTokenExpirationMs());
        response.setUsuarioId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setNegocioActivo(negocioActivo);
        response.setNegocios(negociosDto);

        return response;
    }

    private void crearCategoriasIniciales(Negocio negocio) {
        List<String> nombres;
        if ("RESTAURANTE".equalsIgnoreCase(negocio.getTipoNegocio())) {
            nombres = List.of("Platos Fuertes", "Bebidas", "Entradas y Acompañamientos", "Postres", "Ingredientes de Cocina");
        } else if ("ROPA".equalsIgnoreCase(negocio.getTipoNegocio())) {
            nombres = List.of("Prendas Superiores", "Prendas Inferiores", "Calzado", "Accesorios", "Ropa Interior");
        } else {
            nombres = List.of("Alimentos Básicos", "Higiene y Hogar", "Bebidas y Snacks", "Otros");
        }

        for (String nombre : nombres) {
            Categoria cat = new Categoria();
            cat.setNegocio(negocio);
            cat.setNombre(nombre);
            cat.setDescripcion("Categoría inicial de " + nombre);
            cat.setColorHex("#087F5B");
            categoriaRepository.save(cat);
        }
    }
}
