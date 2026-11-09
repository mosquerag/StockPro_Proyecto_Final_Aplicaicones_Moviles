package com.stockpro.repository;

import com.stockpro.model.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {
    List<Notificacion> findByNegocioIdAndLeidaFalseOrderByCreatedAtDesc(UUID negocioId);
    long countByNegocioIdAndLeidaFalse(UUID negocioId);
}
