package com.stockpro.security;

import com.stockpro.model.entity.MiembroNegocio;
import com.stockpro.model.entity.Usuario;
import com.stockpro.repository.MiembroNegocioRepository;
import com.stockpro.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final MiembroNegocioRepository miembroNegocioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, MiembroNegocioRepository miembroNegocioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.miembroNegocioRepository = miembroNegocioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        List<MiembroNegocio> membresias = miembroNegocioRepository.findByUsuarioId(usuario.getId());
        String rol = membresias.isEmpty() ? "USUARIO" : membresias.get(0).getRol();

        return new UserPrincipal(usuario, rol);
    }
}
