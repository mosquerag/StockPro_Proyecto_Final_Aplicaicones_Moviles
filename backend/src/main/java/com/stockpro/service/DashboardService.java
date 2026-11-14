package com.stockpro.service;

import com.stockpro.dto.dashboard.AlertasResumenDto;
import com.stockpro.dto.dashboard.DashboardDto;
import com.stockpro.dto.inbound.EntradaPrevistaDto;
import com.stockpro.dto.lot.VencimientoAlertDto;
import com.stockpro.dto.product.ProductoDto;
import com.stockpro.dto.sale.VentaDto;
import com.stockpro.model.entity.*;
import com.stockpro.repository.*;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final EntradaPrevistaRepository entradaRepository;
    private final NegocioRepository negocioRepository;
    private final CurrencyService currencyService;
    private final SecurityUtils securityUtils;

    public DashboardService(
            VentaRepository ventaRepository,
            ProductoRepository productoRepository,
            LoteRepository loteRepository,
            EntradaPrevistaRepository entradaRepository,
            NegocioRepository negocioRepository,
            CurrencyService currencyService,
            SecurityUtils securityUtils) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.entradaRepository = entradaRepository;
        this.negocioRepository = negocioRepository;
        this.currencyService = currencyService;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(String monedaSolicitada) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));

        String moneda = (monedaSolicitada != null && !monedaSolicitada.isBlank())
                ? monedaSolicitada.toUpperCase()
                : negocio.getMonedaBase();

        DashboardDto dto = new DashboardDto();
        dto.setNegocioNombre(negocio.getNombre());
        dto.setMoneda(moneda);

        OffsetDateTime ahora = OffsetDateTime.now(ZoneOffset.UTC);
        LocalDate today = LocalDate.now();

        // Rango de fechas por período calendario
        OffsetDateTime inicioHoy = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime inicioSemanal = today.with(java.time.DayOfWeek.MONDAY).atStartOfDay().atOffset(ZoneOffset.UTC);
        int dayOfMonth = today.getDayOfMonth();
        LocalDate inicioQDate = dayOfMonth <= 15 ? today.withDayOfMonth(1) : today.withDayOfMonth(16);
        OffsetDateTime inicioQuincenal = inicioQDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime inicioMensual = today.withDayOfMonth(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime inicioAnual = today.withDayOfYear(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<Venta> ventasHoy = ventaRepository.findVentasCompletadasConDetalles(negocioId, inicioHoy, ahora);

        BigDecimal totalVentas = BigDecimal.ZERO;
        for (Venta v : ventasHoy) {
            BigDecimal tasaConversion = currencyService.getEffectiveRate(v.getMonedaVenta(), moneda, null);
            totalVentas = totalVentas.add(v.getTotalVenta().multiply(tasaConversion));
        }

        BigDecimal gHoy = calcularGananciaVentas(negocioId, inicioHoy, ahora, moneda);
        BigDecimal gSemanal = calcularGananciaVentas(negocioId, inicioSemanal, ahora, moneda);
        BigDecimal gQuincenal = calcularGananciaVentas(negocioId, inicioQuincenal, ahora, moneda);
        BigDecimal gMensual = calcularGananciaVentas(negocioId, inicioMensual, ahora, moneda);
        BigDecimal gAnual = calcularGananciaVentas(negocioId, inicioAnual, ahora, moneda);

        dto.setVentasHoy(currencyService.roundCurrency(totalVentas, moneda));
        dto.setGananciasEstimadasHoy(gHoy);
        dto.setGananciasHoy(gHoy);
        dto.setGananciasSemanal(gSemanal);
        dto.setGananciasQuincenal(gQuincenal);
        dto.setGananciasMensual(gMensual);
        dto.setGananciasAnual(gAnual);
        dto.setCantidadVentasHoy(ventasHoy.size());

        // Cantidad de productos activos
        dto.setCantidadProductos(productoRepository.countByNegocioIdAndActivoTrue(negocioId));

        // Alertas de inventario
        AlertasResumenDto alertasDto = new AlertasResumenDto();

        List<Producto> stockBajo = productoRepository.findStockBajo(negocioId);
        alertasDto.setStockBajoCount(stockBajo.size());
        alertasDto.setStockBajo(stockBajo.stream().map(ProductoDto::new).collect(Collectors.toList()));

        LocalDate hoy = LocalDate.now();
        List<Lote> porVencer = loteRepository.findProximosAVencer(negocioId, hoy, hoy.plusDays(7));
        alertasDto.setPorVencerCount(porVencer.size());
        alertasDto.setPorVencer(porVencer.stream().map(l -> toVencimientoDto(l, hoy)).collect(Collectors.toList()));

        List<Lote> vencidos = loteRepository.findVencidos(negocioId, hoy);
        alertasDto.setVencidosCount(vencidos.size());
        alertasDto.setVencidos(vencidos.stream().map(l -> toVencimientoDto(l, hoy)).collect(Collectors.toList()));

        alertasDto.setTotalAlertas(stockBajo.size() + porVencer.size() + vencidos.size());
        dto.setAlertas(alertasDto);

        // Últimas 5 ventas
        dto.setUltimasVentas(ventaRepository.findUltimasVentas(negocioId).stream()
                .map(VentaDto::new)
                .collect(Collectors.toList()));

        // Entradas de hoy (mapeo directo y seguro)
        List<EntradaPrevista> listaHoy = entradaRepository.findByNegocioIdAndFechaPrevistaOrderByCreatedAtDesc(negocioId, hoy);
        List<EntradaPrevistaDto> dtosHoy = new ArrayList<>();
        if (listaHoy != null) {
            for (EntradaPrevista ep : listaHoy) {
                dtosHoy.add(new EntradaPrevistaDto(ep));
            }
        }
        dto.setEntradasHoy(dtosHoy);

        // Entradas de mañana (mapeo directo y seguro)
        List<EntradaPrevista> listaManana = entradaRepository.findByNegocioIdAndFechaPrevistaOrderByCreatedAtDesc(negocioId, hoy.plusDays(1));
        List<EntradaPrevistaDto> dtosManana = new ArrayList<>();
        if (listaManana != null) {
            for (EntradaPrevista ep : listaManana) {
                dtosManana.add(new EntradaPrevistaDto(ep));
            }
        }
        dto.setEntradasManana(dtosManana);

        return dto;
    }

    private VencimientoAlertDto toVencimientoDto(Lote l, LocalDate hoy) {
        long dias = l.getFechaVencimiento() != null
                ? ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento())
                : 0;
        String estado = dias < 0 ? "VENCIDO" : "PROXIMO_VENCER";
        Producto p = l.getProducto();
        String categoria = (p != null && p.getCategoria() != null) ? p.getCategoria().getNombre() : null;
        return new VencimientoAlertDto(
                l.getId(),
                p != null ? p.getId() : null,
                p != null ? p.getNombre() : null,
                categoria,
                l.getNumeroLote(),
                l.getCantidadDisponible(),
                l.getFechaVencimiento(),
                dias,
                estado);
    }

    private BigDecimal calcularGananciaVentas(UUID negocioId, OffsetDateTime inicio, OffsetDateTime fin, String moneda) {
        List<Venta> ventas = ventaRepository.findVentasCompletadasConDetalles(negocioId, inicio, fin);
        BigDecimal gananciasTotal = BigDecimal.ZERO;

        for (Venta v : ventas) {
            BigDecimal tasaConversion = currencyService.getEffectiveRate(v.getMonedaVenta(), moneda, null);
            BigDecimal costoVenta = BigDecimal.ZERO;
            if (v.getDetalles() != null) {
                for (DetalleVenta det : v.getDetalles()) {
                    BigDecimal cUnit = det.getCostoUnitario() != null ? det.getCostoUnitario() : BigDecimal.ZERO;
                    costoVenta = costoVenta.add(cUnit.multiply(det.getCantidad()));
                }
            }
            BigDecimal costoConvertido = costoVenta.multiply(tasaConversion);
            BigDecimal baseVenta = (v.getSubtotalBase().subtract(v.getTotalDescuentos())).multiply(tasaConversion);
            BigDecimal ganancia = baseVenta.subtract(costoConvertido);
            gananciasTotal = gananciasTotal.add(ganancia);
        }

        return currencyService.roundCurrency(gananciasTotal, moneda);
    }
}