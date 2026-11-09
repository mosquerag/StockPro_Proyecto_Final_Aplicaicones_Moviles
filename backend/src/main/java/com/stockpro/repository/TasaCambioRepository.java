package com.stockpro.repository;

import com.stockpro.model.entity.TasaCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TasaCambioRepository extends JpaRepository<TasaCambio, UUID> {
    Optional<TasaCambio> findByMonedaOrigenAndMonedaDestino(String monedaOrigen, String monedaDestino);
}
