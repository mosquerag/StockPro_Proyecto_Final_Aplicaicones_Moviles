package com.stockpro.repository;

import com.stockpro.model.entity.MiembroNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MiembroNegocioRepository extends JpaRepository<MiembroNegocio, UUID> {
    List<MiembroNegocio> findByUsuarioId(UUID usuarioId);
    Optional<MiembroNegocio> findByNegocioIdAndUsuarioId(UUID negocioId, UUID usuarioId);

    @Query("SELECT m FROM MiembroNegocio m JOIN FETCH m.negocio JOIN FETCH m.usuario WHERE LOWER(m.usuario.correo) = LOWER(:correo)")
    List<MiembroNegocio> findAllByUsuarioCorreoWithNegocio(@Param("correo") String correo);
}
