package com.stockpro.repository;

import com.stockpro.model.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {
    List<Auditoria> findByNegocioIdOrderByCreatedAtDesc(UUID negocioId);
}
