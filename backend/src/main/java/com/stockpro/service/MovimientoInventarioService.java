package com.stockpro.service;

import com.stockpro.dto.movement.CreateMovimientoRequest;
import com.stockpro.dto.movement.MovimientoInventarioDto;
import com.stockpro.model.entity.*;
import com.stockpro.repository.LoteRepository;
import com.stockpro.repository.MovimientoInventarioRepository;
import com.stockpro.repository.NegocioRepository;
import com.stockpro.repository.ProductoRepository;
import com.stockpro.repository.UsuarioRepository;
import com.stockpro.exception.*;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovimientoInventarioService {

    private static final Set<String> TIPOS_POSITIVOS = Set.of("ENTRADA", "AJUSTE_POSITIVO", "DEVOLUCION");
    private static final Set<String> TIPOS_NEGATIVOS = Set.of("PERDIDA", "DANADO", "AJUSTE_NEGATIVO", "VENTA");

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;

    public MovimientoInventarioService(
            MovimientoInventarioRepository movimientoRepository,
            ProductoRepository productoRepository,
            LoteRepository loteRepository,
            NegocioRepository negocioRepository,
            UsuarioRepository usuarioRepository,
            SecurityUtils securityUtils) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.negocioRepository = negocioRepository;
        this.usuarioRepository = usuarioRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Registra un movimiento manual o de control de inventario (ENTRADA, PERDIDA, DANADO, AJUSTE_POSITIVO, AJUSTE_NEGATIVO, DEVOLUCION).
     * El stock del producto y del lote solo cambia por este método o por ventas transaccionales.
     */
    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventarioDto createMovement(CreateMovimientoRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        UUID usuarioId = securityUtils.getCurrentUsuarioId();

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con ID: " + negocioId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        Producto producto = productoRepository.findByIdAndNegocioId(request.getProductoId(), negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o no pertenece al negocio: " + request.getProductoId()));

        String tipo = request.getTipoMovimiento().toUpperCase();
        if (!TIPOS_POSITIVOS.contains(tipo) && !TIPOS_NEGATIVOS.contains(tipo)) {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + tipo);
        }

        BigDecimal cantidad = request.getCantidad();
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        BigDecimal stockAnterior = producto.getStockActual();
        BigDecimal stockPosterior;

        boolean isAumento = TIPOS_POSITIVOS.contains(tipo);
        if (isAumento) {
            stockPosterior = stockAnterior.add(cantidad);
        } else {
            if (!Boolean.TRUE.equals(negocio.getPermiteStockNegativo()) && stockAnterior.compareTo(cantidad) < 0) {
                throw new StockInsuficienteException(String.format(
                        "Stock insuficiente para registrar %s. Disponible: %s, Solicitado: %s",
                        tipo, stockAnterior, cantidad
                ));
            }
            stockPosterior = stockAnterior.subtract(cantidad);
        }

        producto.setStockActual(stockPosterior);
        productoRepository.save(producto);

        // Si se vincula un lote específico
        Lote lote = null;
        if (request.getLoteId() != null) {
            lote = loteRepository.findById(request.getLoteId())
                    .filter(l -> l.getProducto().getId().equals(producto.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado o no pertenece al producto seleccionado"));

            if (isAumento) {
                lote.setCantidadDisponible(lote.getCantidadDisponible().add(cantidad));
                if ("AGOTADO".equals(lote.getEstado())) {
                    lote.setEstado("ACTIVO");
                }
            } else {
                if (lote.getCantidadDisponible().compareTo(cantidad) < 0 && !Boolean.TRUE.equals(negocio.getPermiteStockNegativo())) {
                    throw new StockInsuficienteException("Cantidad disponible insuficiente en el lote " + lote.getNumeroLote());
                }
                BigDecimal nuevoLoteDisp = lote.getCantidadDisponible().subtract(cantidad);
                lote.setCantidadDisponible(nuevoLoteDisp);
                if (nuevoLoteDisp.compareTo(BigDecimal.ZERO) <= 0) {
                    lote.setEstado("AGOTADO");
                }
            }
            loteRepository.save(lote);
        }

        // Registrar el movimiento de auditoría
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocio(negocio);
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockPosterior(stockPosterior);
        movimiento.setCostoReferencia(producto.getCosto() != null ? producto.getCosto() : BigDecimal.ZERO);
        movimiento.setMotivo(request.getMotivo());
        movimiento.setUsuario(usuario);
        movimiento.setReferenciaTipo("MOVIMIENTO_MANUAL");
        movimiento.setFechaMovimiento(OffsetDateTime.now());

        MovimientoInventario guardado = movimientoRepository.save(movimiento);
        return new MovimientoInventarioDto(guardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioDto> listMovements(OffsetDateTime inicio, OffsetDateTime fin) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        if (inicio != null && fin != null) {
            return movimientoRepository.findByNegocioIdAndFechaMovimientoBetweenOrderByFechaMovimientoDesc(negocioId, inicio, fin)
                    .stream()
                    .map(MovimientoInventarioDto::new)
                    .collect(Collectors.toList());
        } else {
            return movimientoRepository.findByNegocioIdOrderByFechaMovimientoDesc(negocioId)
                    .stream()
                    .map(MovimientoInventarioDto::new)
                    .collect(Collectors.toList());
        }
    }
}
