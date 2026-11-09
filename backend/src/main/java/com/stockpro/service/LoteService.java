package com.stockpro.service;

import com.stockpro.dto.lot.BajaLoteRequest;
import com.stockpro.dto.lot.CreateLoteRequest;
import com.stockpro.dto.lot.LoteDto;
import com.stockpro.dto.lot.VencimientoAlertDto;
import com.stockpro.model.entity.*;
import com.stockpro.repository.*;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionRepository notificacionRepository;
    private final SecurityUtils securityUtils;

    public LoteService(
            LoteRepository loteRepository,
            ProductoRepository productoRepository,
            MovimientoInventarioRepository movimientoRepository,
            UsuarioRepository usuarioRepository,
            NotificacionRepository notificacionRepository,
            SecurityUtils securityUtils
    ) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionRepository = notificacionRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<LoteDto> listLotesByProduct(UUID productoId) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Producto producto = productoRepository.findByIdAndNegocioId(productoId, negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en su negocio"));

        return loteRepository.findByProductoIdAndEstadoOrderByFechaVencimientoAsc(producto.getId(), "ACTIVO")
                .stream()
                .map(LoteDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoteDto createLote(CreateLoteRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        UUID usuarioId = securityUtils.getCurrentUsuarioId();

        Producto producto = productoRepository.findByIdAndNegocioId(request.getProductoId(), negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en su negocio"));

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        // 1. Guardar el Lote
        Lote lote = new Lote();
        lote.setNegocio(producto.getNegocio());
        lote.setProducto(producto);
        lote.setNumeroLote(request.getNumeroLote().trim());
        lote.setCantidadInicial(request.getCantidad());
        lote.setCantidadDisponible(request.getCantidad());
        lote.setFechaIngreso(LocalDate.now());
        lote.setFechaVencimiento(request.getFechaVencimiento());
        lote.setEstado("ACTIVO");
        lote = loteRepository.save(lote);

        // 2. Registrar movimiento de inventario obligatorio (ENTRADA)
        BigDecimal stockAnterior = producto.getStockActual();
        BigDecimal stockPosterior = stockAnterior.add(request.getCantidad());

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocio(producto.getNegocio());
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(request.getCantidad());
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockPosterior(stockPosterior);
        movimiento.setCostoReferencia(producto.getCosto());
        movimiento.setMotivo(request.getMotivo() != null ? request.getMotivo() : "Entrada por recepción de nuevo lote");
        movimiento.setUsuario(usuario);
        movimiento.setReferenciaTipo("LOTE");
        movimiento.setReferenciaId(lote.getId());
        movimientoRepository.save(movimiento);

        // 3. Actualizar el stock del producto de manera transaccional
        producto.setStockActual(stockPosterior);
        productoRepository.save(producto);

        return new LoteDto(lote);
    }

    @Transactional
    public void darDeBajaLote(UUID loteId, BajaLoteRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        UUID usuarioId = securityUtils.getCurrentUsuarioId();

        Lote lote = loteRepository.findById(loteId)
                .filter(l -> l.getNegocio().getId().equals(negocioId))
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado en su negocio"));

        if (lote.getCantidadDisponible().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("El lote ya se encuentra agotado o sin existencias disponibles");
        }

        BigDecimal cantidadBaja = lote.getCantidadDisponible();
        Producto producto = lote.getProducto();
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        // 1. Descontar lote y marcar estado
        lote.setCantidadDisponible(BigDecimal.ZERO);
        lote.setEstado("DADO_DE_BAJA");
        loteRepository.save(lote);

        // 2. Descontar del stock actual del producto
        BigDecimal stockAnterior = producto.getStockActual();
        BigDecimal stockPosterior = stockAnterior.subtract(cantidadBaja);
        if (stockPosterior.compareTo(BigDecimal.ZERO) < 0 && !Boolean.TRUE.equals(producto.getNegocio().getPermiteStockNegativo())) {
            stockPosterior = BigDecimal.ZERO;
        }
        producto.setStockActual(stockPosterior);
        productoRepository.save(producto);

        // 3. Registrar movimiento de baja (PERDIDA)
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocio(producto.getNegocio());
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento("PERDIDA");
        movimiento.setCantidad(cantidadBaja);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockPosterior(stockPosterior);
        movimiento.setCostoReferencia(producto.getCosto());
        movimiento.setMotivo(request.getMotivo() != null ? request.getMotivo() : "Baja de existencias por vencimiento/deterioro");
        movimiento.setUsuario(usuario);
        movimiento.setReferenciaTipo("BAJA_LOTE");
        movimiento.setReferenciaId(lote.getId());
        movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public List<VencimientoAlertDto> getProximosAVencer() {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        LocalDate hoy = LocalDate.now();
        LocalDate limite7Dias = hoy.plusDays(7);

        return loteRepository.findProximosAVencer(negocioId, hoy, limite7Dias).stream().map(lote -> {
            long diasRestantes = ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento());
            return new VencimientoAlertDto(
                    lote.getId(),
                    lote.getProducto().getId(),
                    lote.getProducto().getNombre(),
                    lote.getProducto().getCategoria() != null ? lote.getProducto().getCategoria().getNombre() : "General",
                    lote.getNumeroLote(),
                    lote.getCantidadDisponible(),
                    lote.getFechaVencimiento(),
                    diasRestantes,
                    "PROXIMO_VENCER"
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VencimientoAlertDto> getVencidos() {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        LocalDate hoy = LocalDate.now();

        return loteRepository.findVencidos(negocioId, hoy).stream().map(lote -> {
            long diasPasados = ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento());
            return new VencimientoAlertDto(
                    lote.getId(),
                    lote.getProducto().getId(),
                    lote.getProducto().getNombre(),
                    lote.getProducto().getCategoria() != null ? lote.getProducto().getCategoria().getNombre() : "General",
                    lote.getNumeroLote(),
                    lote.getCantidadDisponible(),
                    lote.getFechaVencimiento(),
                    diasPasados,
                    "VENCIDO"
            );
        }).collect(Collectors.toList());
    }
}
