package com.stockpro.security;

import com.stockpro.model.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String nombre;
    private final String correo;
    private final String password;
    private final boolean activo;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Usuario usuario, String rol) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
        this.password = usuario.getPasswordHash();
        this.activo = usuario.getActivo();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return correo; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return activo; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return activo; }
}
