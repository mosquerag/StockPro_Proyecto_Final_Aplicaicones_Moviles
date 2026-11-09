package com.stockpro.repository;

import com.stockpro.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
}
