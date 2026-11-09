package com.stockpro.service;

import com.stockpro.dto.inbound.*;
import com.stockpro.model.entity.*;
import com.stockpro.repository.*;
import com.stockpro.exception.*;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntradaPrevistaService {

    private final EntradaPrevistaRepository entradaRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;

    public EntradaPrevistaService(
            EntradaPrevistaRepository entradaRepository,
            ProductoRepository productoRepository,
            LoteRepository loteRepository,
            MovimientoInventarioRepository movimientoRepository,
            NegocioRepository negocioRepository,
            UsuarioRepository usuarioRepository,
            SecurityUtils securityUtils) {
        this.entradaRepository = entradaRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.negocioRepository = negocioRepository;
        this.usuarioRepository = usuarioRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Crea una entrada prevista (hoy, mañana u otra fecha).
     * REGLA CLAVE: No altera el stock ni crea lotes hasta que sea marcada como recibida.
     */
    @Transactional(rollbackFor = Exception.class)
    public EntradaPrevistaDto createEntrada(CreateEntradaPrevistaRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con ID: " + negocioId));

        EntradaPrevista entrada = new EntradaPrevista();
        entrada.setNegocio(negocio);
        entrada.setFechaPrevista(request.getFechaPrevista());
        entrada.setEstado("PENDIENTE");

        // Referencia u origen libre (sin entidad de proveedores)
        String orderCode = "ENT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        if (request.getOrigen() != null && !request.getOrigen().isBlank()) {
            entrada.setNumeroOrden(orderCode + " [" + request.getOrigen().trim() + "]");
        } else {
            entrada.setNumeroOrden(orderCode);
        }

        entrada.setNotas(request.getNotas());

        BigDecimal totalEstimado = BigDecimal.ZERO;

        for (CreateDetalleEntradaRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findByIdAndNegocioId(itemReq.getProductoId(), negocioId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o no pertenece al negocio: " + itemReq.getProductoId()));

            DetalleEntradaPrevista detalle = new DetalleEntradaPrevista();
            detalle.setEntradaPrevista(entrada);
            detalle.setProducto(producto);
            detalle.setCantidadEsperada(itemReq.getCantidadEsperada());
            detalle.setCantidadRecibida(BigDecimal.ZERO);

            BigDecimal costoUnitario = itemReq.getCostoUnitario() != null
                    ? itemReq.getCostoUnitario()
                    : (producto.getCosto() != null ? producto.getCosto() : BigDecimal.ZERO);
            detalle.setCostoUnitario(costoUnitario);

            BigDecimal subtotal = itemReq.getCantidadEsperada().multiply(costoUnitario);
            totalEstimado = totalEstimado.add(subtotal);

            entrada.getDetalles().add(detalle);
        }

        entrada.setTotalEstimado(totalEstimado);
        EntradaPrevista guardada = entradaRepository.save(entrada);
        return new EntradaPrevistaDto(guardada);
    }

    /**
     * Marca la entrada prevista como RECIBIDA en una sola transacción:
     * 1. Actualiza cantidades recibidas.
     * 2. Si el producto maneja lotes o se especifica vencimiento, crea el Lote correspondiente.
     * 3. Suma el stock actual del producto.
     * 4. Registra un MovimientoInventario tipo 'ENTRADA'.
     */
    @Transactional(rollbackFor = Exception.class)
    public EntradaPrevistaDto receiveEntrada(UUID entradaId, ReceiveEntradaRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        UUID usuarioId = securityUtils.getCurrentUsuarioId();

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        EntradaPrevista entrada = entradaRepository.findByIdAndNegocioId(entradaId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("ENTRADA_NO_ENCONTRADA", "Entrada prevista no encontrada con ID: " + entradaId));

        if (!"PENDIENTE".equalsIgnoreCase(entrada.getEstado())) {
            throw new OperacionInvalidaException("ENTRADA_YA_RECIBIDA", "La entrada ya se encuentra en estado: " + entrada.getEstado());
        }

        // Mapear items recibidos opcionales si se envió un detalle específico
        Map<UUID, ReceiveItemRequest> itemOverrides = new HashMap<>();
        if (request != null && request.getItems() != null) {
            for (ReceiveItemRequest rItem : request.getItems()) {
                itemOverrides.put(rItem.getProductoId(), rItem);
            }
        }

        for (DetalleEntradaPrevista det : entrada.getDetalles()) {
            Producto producto = det.getProducto();
            ReceiveItemRequest rOverride = itemOverrides.get(producto.getId());

            BigDecimal cantidadEfectiva = (rOverride != null && rOverride.getCantidadRecibida() != null)
                    ? rOverride.getCantidadRecibida()
                    : det.getCantidadEsperada();

            det.setCantidadRecibida(cantidadEfectiva);

            Lote loteCreado = null;
            boolean requiereLote = Boolean.TRUE.equals(producto.getManejaLotes()) ||
                    (rOverride != null && rOverride.getFechaVencimiento() != null);

            if (requiereLote) {
                loteCreado = new Lote();
                loteCreado.setProducto(producto);
                loteCreado.setNegocio(negocio);

                String numLote = (rOverride != null && rOverride.getNumeroLote() != null && !rOverride.getNumeroLote().isBlank())
                        ? rOverride.getNumeroLote().trim()
                        : "LOT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                loteCreado.setNumeroLote(numLote);

                if (rOverride != null && rOverride.getFechaVencimiento() != null) {
                    loteCreado.setFechaVencimiento(rOverride.getFechaVencimiento());
                } else {
                    // Por defecto 6 meses si es perecedero y no se ingresó fecha
                    loteCreado.setFechaVencimiento(LocalDate.now().plusMonths(6));
                }

                loteCreado.setCantidadInicial(cantidadEfectiva);
                loteCreado.setCantidadDisponible(cantidadEfectiva);
                loteCreado.setCostoUnitario(det.getCostoUnitario());
                loteCreado.setEstado("ACTIVO");
                loteCreado = loteRepository.save(loteCreado);
            }

            // Sumar al stock del producto
            BigDecimal stockAnterior = producto.getStockActual();
            BigDecimal stockPosterior = stockAnterior.add(cantidadEfectiva);
            producto.setStockActual(stockPosterior);
            productoRepository.save(producto);

            // Generar MovimientoInventario tipo ENTRADA
            MovimientoInventario mov = new MovimientoInventario();
            mov.setNegocio(negocio);
            mov.setProducto(producto);
            mov.setLote(loteCreado);
            mov.setTipoMovimiento("ENTRADA");
            mov.setCantidad(cantidadEfectiva);
            mov.setStockAnterior(stockAnterior);
            mov.setStockPosterior(stockPosterior);
            mov.setCostoReferencia(det.getCostoUnitario());
            mov.setMotivo("Recepción de mercancía orden: " + entrada.getNumeroOrden());
            mov.setUsuario(usuario);
            mov.setReferenciaTipo("ENTRADA_PREVISTA");
            mov.setReferenciaId(entrada.getId());
            mov.setFechaMovimiento(OffsetDateTime.now());
            movimientoRepository.save(mov);
        }

        entrada.setEstado("RECIBIDA");
        entrada.setFechaRecibida(OffsetDateTime.now());
        if (request != null && request.getNotas() != null && !request.getNotas().isBlank()) {
            entrada.setNotas((entrada.getNotas() != null ? entrada.getNotas() + " | " : "") + request.getNotas());
        }

        EntradaPrevista actualizada = entradaRepository.save(entrada);
        return new EntradaPrevistaDto(actualizada);
    }

    @Transactional(readOnly = true)
    public List<EntradaPrevistaDto> listHoy() {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        return entradaRepository.findByNegocioIdAndFechaPrevistaOrderByCreatedAtDesc(negocioId, LocalDate.now())
                .stream()
                .map(EntradaPrevistaDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntradaPrevistaDto> listManana() {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        return entradaRepository.findByNegocioIdAndFechaPrevistaOrderByCreatedAtDesc(negocioId, LocalDate.now().plusDays(1))
                .stream()
                .map(EntradaPrevistaDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EntradaPrevistaDto getById(UUID id) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        EntradaPrevista entrada = entradaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Entrada prevista no encontrada con ID: " + id));
        return new EntradaPrevistaDto(entrada);
    }
}
