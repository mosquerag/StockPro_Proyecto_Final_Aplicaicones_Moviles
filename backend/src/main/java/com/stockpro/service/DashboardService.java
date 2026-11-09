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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

        // Rango del día de hoy
        OffsetDateTime inicioHoy = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        List<Venta> ventasHoy = ventaRepository.findVentasCompletadasConDetalles(negocioId, inicioHoy, finHoy);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal gananciasEstimadas = BigDecimal.ZERO;

        for (Venta v : ventasHoy) {
            BigDecimal tasaConversion = currencyService.getEffectiveRate(v.getMonedaVenta(), moneda, null);

            BigDecimal ventaConvertida = v.getTotalVenta().multiply(tasaConversion);
            totalVentas = totalVentas.add(ventaConvertida);

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
            gananciasEstimadas = gananciasEstimadas.add(ganancia);
        }

        dto.setVentasHoy(currencyService.roundCurrency(totalVentas, moneda));
        dto.setGananciasEstimadasHoy(currencyService.roundCurrency(gananciasEstimadas, moneda));

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
        alertasDto.setPorVencer(porVencer.stream().map(VencimientoAlertDto::new).collect(Collectors.toList()));

        List<Lote> vencidos = loteRepository.findVencidos(negocioId, hoy);
        alertasDto.setVencidosCount(vencidos.size());
        alertasDto.setVencidos(vencidos.stream().map(VencimientoAlertDto::new).collect(Collectors.toList()));

        alertasDto.setTotalAlertas(stockBajo.size() + porVencer.size() + vencidos.size());
        dto.setAlertas(alertasDto);

        // Últimas 5 ventas
        dto.setUltimasVentas(ventaRepository.findUltimasVentas(negocioId).stream()
                .map(VentaDto::new)
                .collect(Collectors.toList()));

        // Entradas de hoy y de mañana
        dto.setEntradasHoy(entradaRepository.findByNegocioIdAndFechaPrevistaOrderByCreatedAtDesc(negocioId, hoy).stream()
                .map(EntradaPrevistaDto::new)
                .collect(Collectors.toList()));

        dto.setEntradasManana(entradaRepository.findByNegocioIdAndFechaPrevistaOrderByCreatedAtDesc(negocioId, hoy.plusDays(1)).stream()
                .map(EntradaPrevistaDto::new)
                .collect(Collectors.toList()));

        return dto;
    }
}
