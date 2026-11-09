package com.stockpro.service;

import com.stockpro.dto.sale.CreateVentaItemRequest;
import com.stockpro.dto.sale.CreateVentaRequest;
import com.stockpro.dto.sale.VentaDto;
import com.stockpro.model.entity.*;
import com.stockpro.repository.*;
import com.stockpro.exception.*;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TarifaImpuestoRepository tarifaImpuestoRepository;
    private final NotificacionRepository notificacionRepository;
    private final CurrencyService currencyService;
    private final SecurityUtils securityUtils;

    public VentaService(
            VentaRepository ventaRepository,
            ProductoRepository productoRepository,
            LoteRepository loteRepository,
            MovimientoInventarioRepository movimientoInventarioRepository,
            NegocioRepository negocioRepository,
            UsuarioRepository usuarioRepository,
            TarifaImpuestoRepository tarifaImpuestoRepository,
            NotificacionRepository notificacionRepository,
            CurrencyService currencyService,
            SecurityUtils securityUtils) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.negocioRepository = negocioRepository;
        this.usuarioRepository = usuarioRepository;
        this.tarifaImpuestoRepository = tarifaImpuestoRepository;
        this.notificacionRepository = notificacionRepository;
        this.currencyService = currencyService;
        this.securityUtils = securityUtils;
    }

    /**
     * Registra una venta transaccional:
     * 1. Descuenta stock con estrategia FEFO en productos con lotes.
     * 2. No permite stock negativo salvo configuración expresa.
     * 3. Aplica tarifas de impuesto 2026 fijadas en cada línea.
     * 4. Registra los movimientos de inventario tipo 'VENTA'.
     * 5. Convierte y congela la tasa de cambio y moneda de la transacción.
     * Todo ocurre dentro de una única transacción atómica; si algo falla, no se guarda nada.
     */
    @Transactional(rollbackFor = Exception.class)
    public VentaDto createSale(CreateVentaRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        UUID usuarioId = securityUtils.getCurrentUsuarioId();

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con ID: " + negocioId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        // Moneda y tasa de cambio
        String monedaBase = negocio.getMonedaBase() != null ? negocio.getMonedaBase().toUpperCase() : "COP";
        String monedaVenta = (request.getMonedaVenta() != null && !request.getMonedaVenta().isBlank())
                ? request.getMonedaVenta().toUpperCase()
                : monedaBase;

        BigDecimal tasaCambio = currencyService.getEffectiveRate(monedaBase, monedaVenta, request.getTasaCambioManual());

        // Generar número de comprobante único consecutivo diario
        String voucher = generateVoucherNumber();

        Venta venta = new Venta();
        venta.setNegocio(negocio);
        venta.setUsuario(usuario);
        venta.setNumeroComprobante(voucher);
        venta.setFechaVenta(OffsetDateTime.now());
        venta.setMonedaVenta(monedaVenta);
        venta.setTasaCambioBase(tasaCambio);
        venta.setMetodoPago(request.getMetodoPago() != null ? request.getMetodoPago().toUpperCase() : "EFECTIVO");
        venta.setEstado("COMPLETADA");
        venta.setNotas(request.getNotas());

        // Contexto de seguridad y auditoría de descuentos
        String rolUsuario = securityUtils.getCurrentRol();
        boolean huboDescuento = false;
        String autorizadorDescuento = null;
        BigDecimal maxDescuentoPct = new BigDecimal("0.30"); // 30% configurable por defecto

        // Inicializar acumuladores
        BigDecimal totalSubtotalBruto = BigDecimal.ZERO;
        BigDecimal totalImpuestos = BigDecimal.ZERO;
        BigDecimal totalDescuentos = BigDecimal.ZERO;
        BigDecimal totalVenta = BigDecimal.ZERO;

        // Procesar cada item solicitado
        for (CreateVentaItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findByIdAndNegocioId(itemReq.getProductoId(), negocioId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o no pertenece al negocio: " + itemReq.getProductoId()));

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new IllegalStateException("El producto '" + producto.getNombre() + "' está inactivo y no se puede vender.");
            }

            BigDecimal cantidadRequerida = itemReq.getCantidad();
            if (cantidadRequerida == null || cantidadRequerida.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para el producto: " + producto.getNombre());
            }

            // Validar stock global disponible si el negocio no permite negativos
            if (!Boolean.TRUE.equals(negocio.getPermiteStockNegativo()) &&
                    producto.getStockActual().compareTo(cantidadRequerida) < 0) {
                throw new StockInsuficienteException(String.format(
                        "Stock insuficiente para '%s'. Disponible: %s, Solicitado: %s",
                        producto.getNombre(), producto.getStockActual(), cantidadRequerida
                ));
            }

            // REGLA DE SEGURIDAD: Tomar SIEMPRE el precio oficial del producto guardado en la BD
            BigDecimal precioUnitario = producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO;
            BigDecimal costoUnitario = producto.getCosto() != null ? producto.getCosto() : BigDecimal.ZERO;

            // Validación estricta de descuentos: no confiar en precios arbitrarios enviados por la app
            BigDecimal subtotalBrutoItem = precioUnitario.multiply(cantidadRequerida);
            BigDecimal descuentoItem = BigDecimal.ZERO;

            if (itemReq.getDescuento() != null && itemReq.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
                descuentoItem = itemReq.getDescuento();
            }

            // Si la app envió un precio menor al de catálogo, se evalúa como descuento implícito
            if (itemReq.getPrecioUnitario() != null && itemReq.getPrecioUnitario().compareTo(precioUnitario) < 0) {
                BigDecimal diferencia = precioUnitario.subtract(itemReq.getPrecioUnitario()).multiply(cantidadRequerida);
                descuentoItem = descuentoItem.add(diferencia);
            }

            if (descuentoItem.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El descuento no puede ser negativo para el producto: " + producto.getNombre());
            }

            if (descuentoItem.compareTo(BigDecimal.ZERO) > 0) {
                // REGLA 2: Solo el rol PROPIETARIO está autorizado para aplicar descuentos
                if (rolUsuario != null && !rolUsuario.equalsIgnoreCase("PROPIETARIO") && !rolUsuario.equalsIgnoreCase("ADMIN")) {
                    throw new IllegalArgumentException("Permiso denegado: Solo los usuarios con rol PROPIETARIO pueden aplicar descuentos en las ventas.");
                }

                // Límite máximo configurable (máximo 30% del subtotal bruto)
                BigDecimal maxDescuentoPermitido = subtotalBrutoItem.multiply(maxDescuentoPct);
                if (descuentoItem.compareTo(maxDescuentoPermitido) > 0) {
                    throw new IllegalArgumentException(String.format(
                            "Descuento no permitido para '%s'. El descuento solicitado (%s) excede el máximo permitido del 30%% (%s)",
                            producto.getNombre(), descuentoItem, maxDescuentoPermitido
                    ));
                }

                huboDescuento = true;
                autorizadorDescuento = usuario.getNombre() + " (" + usuario.getCorreo() + ")";
            }

            // Determinar tarifa de impuesto 2026 aplicable
            BigDecimal porcentajeImpuesto = resolveTaxPercentage(negocio, producto);

            // Deducción de stock: FEFO si maneja lotes, directa si no
            if (Boolean.TRUE.equals(producto.getManejaLotes())) {
                List<Lote> activeLots = loteRepository.findByProductoIdAndEstadoOrderByFechaVencimientoAsc(producto.getId(), "ACTIVO");
                BigDecimal cantidadPendiente = cantidadRequerida;

                for (Lote lote : activeLots) {
                    if (cantidadPendiente.compareTo(BigDecimal.ZERO) <= 0) break;
                    if (lote.getCantidadDisponible().compareTo(BigDecimal.ZERO) <= 0) continue;

                    BigDecimal take = lote.getCantidadDisponible().min(cantidadPendiente);
                    lote.setCantidadDisponible(lote.getCantidadDisponible().subtract(take));
                    if (lote.getCantidadDisponible().compareTo(BigDecimal.ZERO) == 0) {
                        lote.setEstado("AGOTADO");
                    }
                    loteRepository.save(lote);

                    cantidadPendiente = cantidadPendiente.subtract(take);

                    // Crear detalle por cada porción de lote
                    DetalleVenta det = buildDetalleVenta(
                            venta, producto, lote, take, precioUnitario, costoUnitario,
                            descuentoItem, porcentajeImpuesto, monedaVenta
                    );
                    venta.getDetalles().add(det);

                    totalSubtotalBruto = totalSubtotalBruto.add(det.getSubtotalBruto());
                    totalImpuestos = totalImpuestos.add(det.getMontoImpuesto());
                    totalDescuentos = totalDescuentos.add(det.getDescuento());
                    totalVenta = totalVenta.add(det.getTotalLinea());

                    descuentoItem = BigDecimal.ZERO; // El descuento se aplica solo una vez en la primera fracción
                }

                if (cantidadPendiente.compareTo(BigDecimal.ZERO) > 0) {
                    if (!Boolean.TRUE.equals(negocio.getPermiteStockNegativo())) {
                        throw new StockInsuficienteException("Lotes activos insuficientes para cubrir el pedido de '" + producto.getNombre() + "'.");
                    } else {
                        // Remanente en negativo sin lote asignado
                        DetalleVenta det = buildDetalleVenta(
                                venta, producto, null, cantidadPendiente, precioUnitario, costoUnitario,
                                descuentoItem, porcentajeImpuesto, monedaVenta
                        );
                        venta.getDetalles().add(det);

                        totalSubtotalBruto = totalSubtotalBruto.add(det.getSubtotalBruto());
                        totalImpuestos = totalImpuestos.add(det.getMontoImpuesto());
                        totalDescuentos = totalDescuentos.add(det.getDescuento());
                        totalVenta = totalVenta.add(det.getTotalLinea());
                    }
                }
            } else {
                // Producto sin lotes
                DetalleVenta det = buildDetalleVenta(
                        venta, producto, null, cantidadRequerida, precioUnitario, costoUnitario,
                        descuentoItem, porcentajeImpuesto, monedaVenta
                );
                venta.getDetalles().add(det);

                totalSubtotalBruto = totalSubtotalBruto.add(det.getSubtotalBruto());
                totalImpuestos = totalImpuestos.add(det.getMontoImpuesto());
                totalDescuentos = totalDescuentos.add(det.getDescuento());
                totalVenta = totalVenta.add(det.getTotalLinea());
            }

            // Actualizar stock del producto
            BigDecimal stockAnterior = producto.getStockActual();
            BigDecimal stockPosterior = stockAnterior.subtract(cantidadRequerida);
            producto.setStockActual(stockPosterior);
            productoRepository.save(producto);

            // Registrar movimiento de inventario oficial
            MovimientoInventario mov = new MovimientoInventario();
            mov.setNegocio(negocio);
            mov.setProducto(producto);
            mov.setTipoMovimiento("VENTA");
            mov.setCantidad(cantidadRequerida);
            mov.setStockAnterior(stockAnterior);
            mov.setStockPosterior(stockPosterior);
            mov.setCostoReferencia(costoUnitario);
            mov.setMotivo("Venta registrada comprobante " + voucher);
            mov.setUsuario(usuario);
            mov.setReferenciaTipo("VENTA");
            mov.setFechaMovimiento(OffsetDateTime.now());
            movimientoInventarioRepository.save(mov);

            // Alerta si el stock cayó por debajo o igual al mínimo
            if (stockPosterior.compareTo(producto.getStockMinimo()) <= 0) {
                Notificacion notif = new Notificacion();
                notif.setNegocio(negocio);
                notif.setTipo("STOCK_BAJO");
                notif.setTitulo("Alerta de stock bajo: " + producto.getNombre());
                notif.setMensaje(String.format("El stock actual (%s) está en o por debajo del mínimo (%s).",
                        stockPosterior.toPlainString(), producto.getStockMinimo().toPlainString()));
                notif.setPrioridad("ALTA");
                notif.setReferenciaTipo("PRODUCTO");
                notif.setReferenciaId(producto.getId());
                notificacionRepository.save(notif);
            }
        }

        // Aplicar reglas de redondeo según moneda (COP: 0 decimales, USD/EUR: 2 decimales)
        venta.setSubtotalBase(currencyService.roundCurrency(totalSubtotalBruto, monedaVenta));
        venta.setTotalImpuestos(currencyService.roundCurrency(totalImpuestos, monedaVenta));
        venta.setTotalDescuentos(currencyService.roundCurrency(totalDescuentos, monedaVenta));
        venta.setTotalVenta(currencyService.roundCurrency(totalVenta, monedaVenta));

        // Registrar auditoría de descuento
        if (huboDescuento && autorizadorDescuento != null) {
            String notasActuales = (venta.getNotas() != null && !venta.getNotas().isBlank()) ? venta.getNotas() + " | " : "";
            venta.setNotas(notasActuales + "[Descuento autorizado por PROPIETARIO: " + autorizadorDescuento + "]");
        }

        Venta guardada = ventaRepository.save(venta);
        return new VentaDto(guardada);
    }

    private DetalleVenta buildDetalleVenta(
            Venta venta, Producto producto, Lote lote, BigDecimal cantidad,
            BigDecimal precioUnitario, BigDecimal costoUnitario, BigDecimal descuento,
            BigDecimal porcentajeImpuesto, String moneda) {

        DetalleVenta det = new DetalleVenta();
        det.setVenta(venta);
        det.setProducto(producto);
        det.setLote(lote);
        det.setCantidad(cantidad);
        det.setPrecioUnitario(precioUnitario);
        det.setCostoUnitario(costoUnitario);

        BigDecimal subtotalBruto = precioUnitario.multiply(cantidad);
        det.setSubtotalBruto(currencyService.roundCurrency(subtotalBruto, moneda));

        det.setDescuento(currencyService.roundCurrency(descuento, moneda));

        det.setCategoriaFiscal(producto.getCategoriaFiscal());
        det.setTarifaImpuestoPorcentaje(porcentajeImpuesto);

        BigDecimal baseImponible = subtotalBruto.subtract(descuento).max(BigDecimal.ZERO);
        BigDecimal montoImpuesto = baseImponible.multiply(porcentajeImpuesto).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        det.setMontoImpuesto(currencyService.roundCurrency(montoImpuesto, moneda));

        BigDecimal totalLinea = baseImponible.add(montoImpuesto);
        det.setTotalLinea(currencyService.roundCurrency(totalLinea, moneda));

        return det;
    }

    private BigDecimal resolveTaxPercentage(Negocio negocio, Producto producto) {
        String pais = negocio.getPais() != null ? negocio.getPais().toUpperCase() : "CO";
        String estadoRegion = negocio.getEstadoRegion();
        String categoriaFiscal = producto.getCategoriaFiscal() != null ? producto.getCategoriaFiscal() : "GENERAL";

        if ("US".equals(pais) && estadoRegion != null && !estadoRegion.isBlank()) {
            Optional<TarifaImpuesto> usRate = tarifaImpuestoRepository
                    .findByPaisAndEstadoRegionAndCategoriaFiscalAndActivoTrue(pais, estadoRegion.toUpperCase(), categoriaFiscal);
            if (usRate.isPresent()) return usRate.get().getPorcentaje();

            // Fallback a GENERAL en ese estado
            Optional<TarifaImpuesto> usGeneral = tarifaImpuestoRepository
                    .findByPaisAndEstadoRegionAndCategoriaFiscalAndActivoTrue(pais, estadoRegion.toUpperCase(), "GENERAL");
            if (usGeneral.isPresent()) return usGeneral.get().getPorcentaje();
        }

        // Búsqueda directa por país y categoría fiscal (Colombia, Italia, etc.)
        Optional<TarifaImpuesto> rate = tarifaImpuestoRepository
                .findByPaisAndCategoriaFiscalAndActivoTrue(pais, categoriaFiscal);
        if (rate.isPresent()) return rate.get().getPorcentaje();

        // Fallback por país a tarifa GENERAL u ORDINARIA
        Optional<TarifaImpuesto> general = tarifaImpuestoRepository
                .findByPaisAndCategoriaFiscalAndActivoTrue(pais, "GENERAL");
        if (general.isPresent()) return general.get().getPorcentaje();

        Optional<TarifaImpuesto> ordinaria = tarifaImpuestoRepository
                .findByPaisAndCategoriaFiscalAndActivoTrue(pais, "ORDINARIA");
        if (ordinaria.isPresent()) return ordinaria.get().getPorcentaje();

        // Tarifas base de seguridad 2026
        if ("CO".equals(pais)) return new BigDecimal("19.00");
        if ("IT".equals(pais)) return new BigDecimal("22.00");
        return BigDecimal.ZERO;
    }

    private String generateVoucherNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "V-" + datePart + "-" + randomSuffix;
    }

    @Transactional(readOnly = true)
    public List<VentaDto> listVentas(OffsetDateTime inicio, OffsetDateTime fin) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        OffsetDateTime start = inicio != null ? inicio : OffsetDateTime.now().minusDays(30);
        OffsetDateTime end = fin != null ? fin : OffsetDateTime.now();

        return ventaRepository.findByNegocioIdAndFechaVentaBetweenOrderByFechaVentaDesc(negocioId, start, end)
                .stream()
                .map(VentaDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VentaDto getVentaById(UUID id) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        Venta v = ventaRepository.findById(id)
                .filter(item -> item.getNegocio().getId().equals(negocioId))
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id));
        return new VentaDto(v);
    }
}
