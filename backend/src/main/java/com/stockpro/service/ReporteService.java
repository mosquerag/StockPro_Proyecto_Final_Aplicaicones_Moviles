package com.stockpro.service;

import com.stockpro.dto.report.*;
import com.stockpro.model.entity.*;
import com.stockpro.repository.MovimientoInventarioRepository;
import com.stockpro.repository.NegocioRepository;
import com.stockpro.repository.ProductoRepository;
import com.stockpro.repository.VentaRepository;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final NegocioRepository negocioRepository;
    private final CurrencyService currencyService;
    private final SecurityUtils securityUtils;

    public ReporteService(
            VentaRepository ventaRepository,
            ProductoRepository productoRepository,
            MovimientoInventarioRepository movimientoRepository,
            NegocioRepository negocioRepository,
            CurrencyService currencyService,
            SecurityUtils securityUtils) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.negocioRepository = negocioRepository;
        this.currencyService = currencyService;
        this.securityUtils = securityUtils;
    }

    /**
     * Genera el reporte financiero y de inventario completo con filtros de tiempo (7D, 30D, 3M)
     * y conversión a moneda elegida (COP, USD, EUR) sin alterar el histórico de la base de datos.
     */
    @Transactional(readOnly = true)
    public ReporteGeneralDto generateReport(String periodoParam, String monedaParam) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado"));

        String periodo = (periodoParam != null && !periodoParam.isBlank()) ? periodoParam.toUpperCase() : "30D";
        String moneda = (monedaParam != null && !monedaParam.isBlank()) ? monedaParam.toUpperCase() : negocio.getMonedaBase();

        OffsetDateTime fin = OffsetDateTime.now();
        OffsetDateTime inicio;
        switch (periodo) {
            case "7D":
                inicio = fin.minusDays(7);
                break;
            case "3M":
                inicio = fin.minusMonths(3);
                break;
            case "30D":
            default:
                periodo = "30D";
                inicio = fin.minusDays(30);
                break;
        }

        ReporteGeneralDto reporte = new ReporteGeneralDto();
        reporte.setPeriodo(periodo);
        reporte.setFechaInicio(inicio);
        reporte.setFechaFin(fin);
        reporte.setMonedaReporte(moneda);

        // 1. Obtener ventas completadas del período con sus líneas
        List<Venta> ventas = ventaRepository.findVentasCompletadasConDetalles(negocioId, inicio, fin);

        // 2. Cálculo de Ganancias e Impuestos
        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalCostos = BigDecimal.ZERO;
        BigDecimal totalGananciaBruta = BigDecimal.ZERO;

        Map<String, ImpuestoAcumulado> impuestoMap = new HashMap<>();
        Map<UUID, MetricaProducto> productoMetricas = new HashMap<>();

        for (Venta v : ventas) {
            BigDecimal tasaConversion = currencyService.getEffectiveRate(v.getMonedaVenta(), moneda, null);

            BigDecimal ventaConv = v.getTotalVenta().multiply(tasaConversion);
            totalVentas = totalVentas.add(ventaConv);

            if (v.getDetalles() != null) {
                for (DetalleVenta det : v.getDetalles()) {
                    BigDecimal cantidad = det.getCantidad();
                    BigDecimal costoUnitario = det.getCostoUnitario() != null ? det.getCostoUnitario() : BigDecimal.ZERO;
                    BigDecimal precioUnitario = det.getPrecioUnitario() != null ? det.getPrecioUnitario() : BigDecimal.ZERO;

                    BigDecimal costoLinea = costoUnitario.multiply(cantidad).multiply(tasaConversion);
                    BigDecimal subtotalBruto = precioUnitario.multiply(cantidad).multiply(tasaConversion);
                    BigDecimal descuento = (det.getDescuento() != null ? det.getDescuento() : BigDecimal.ZERO).multiply(tasaConversion);
                    BigDecimal baseImponible = subtotalBruto.subtract(descuento);
                    BigDecimal gananciaLinea = baseImponible.subtract(costoLinea);

                    totalCostos = totalCostos.add(costoLinea);
                    totalGananciaBruta = totalGananciaBruta.add(gananciaLinea);

                    // Acumulado de impuestos
                    String catFiscal = det.getCategoriaFiscal() != null ? det.getCategoriaFiscal() : "GENERAL";
                    BigDecimal tarifa = det.getTarifaImpuestoPorcentaje() != null ? det.getTarifaImpuestoPorcentaje() : BigDecimal.ZERO;
                    String taxKey = catFiscal + "_" + tarifa.toPlainString();

                    ImpuestoAcumulado imp = impuestoMap.computeIfAbsent(taxKey, k -> new ImpuestoAcumulado(catFiscal, tarifa));
                    imp.base = imp.base.add(baseImponible);
                    imp.monto = imp.monto.add((det.getMontoImpuesto() != null ? det.getMontoImpuesto() : BigDecimal.ZERO).multiply(tasaConversion));

                    // Acumulado por producto
                    if (det.getProducto() != null) {
                        UUID pId = det.getProducto().getId();
                        MetricaProducto mp = productoMetricas.computeIfAbsent(pId, k -> new MetricaProducto(
                                pId,
                                det.getProducto().getNombre(),
                                det.getProducto().getCategoria() != null ? det.getProducto().getCategoria().getNombre() : "General"
                        ));
                        mp.cantidadVendida = mp.cantidadVendida.add(cantidad);
                        mp.facturacion = mp.facturacion.add(baseImponible.add(det.getMontoImpuesto() != null ? det.getMontoImpuesto().multiply(tasaConversion) : BigDecimal.ZERO));
                        mp.costos = mp.costos.add(costoLinea);
                        mp.ganancia = mp.ganancia.add(gananciaLinea);
                    }
                }
            }
        }

        // Armar bloque Ganancias
        GananciasReportDto gananciasDto = new GananciasReportDto();
        gananciasDto.setTotalVentas(currencyService.roundCurrency(totalVentas, moneda));
        gananciasDto.setTotalCostos(currencyService.roundCurrency(totalCostos, moneda));
        gananciasDto.setGananciaBruta(currencyService.roundCurrency(totalGananciaBruta, moneda));
        if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = totalGananciaBruta.divide(totalVentas, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            gananciasDto.setMargenPorcentual(margen.setScale(2, RoundingMode.HALF_UP));
        } else {
            gananciasDto.setMargenPorcentual(BigDecimal.ZERO);
        }
        gananciasDto.setMoneda(moneda);
        reporte.setGanancias(gananciasDto);

        // 3. Productos más y menos vendidos
        List<Producto> todosLosProductos = productoRepository.findByNegocioIdAndActivoTrueOrderByNombreAsc(negocioId);
        for (Producto prod : todosLosProductos) {
            productoMetricas.putIfAbsent(prod.getId(), new MetricaProducto(
                    prod.getId(),
                    prod.getNombre(),
                    prod.getCategoria() != null ? prod.getCategoria().getNombre() : "General"
            ));
        }

        List<MetricaProducto> listaMetricas = new ArrayList<>(productoMetricas.values());

        // Más vendidos (Top 10 descendente por cantidad)
        List<ProductoRankingDto> masVendidos = listaMetricas.stream()
                .filter(m -> m.cantidadVendida.compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.cantidadVendida.compareTo(a.cantidadVendida))
                .limit(10)
                .map(m -> new ProductoRankingDto(
                        m.productoId, m.nombre, m.categoria,
                        m.cantidadVendida,
                        currencyService.roundCurrency(m.facturacion, moneda),
                        currencyService.roundCurrency(m.ganancia, moneda)
                ))
                .collect(Collectors.toList());
        reporte.setMasVendidos(masVendidos);

        // Menos vendidos (Bottom 10 ascendente por cantidad)
        List<ProductoRankingDto> menosVendidos = listaMetricas.stream()
                .sorted(Comparator.comparing(a -> a.cantidadVendida))
                .limit(10)
                .map(m -> new ProductoRankingDto(
                        m.productoId, m.nombre, m.categoria,
                        m.cantidadVendida,
                        currencyService.roundCurrency(m.facturacion, moneda),
                        currencyService.roundCurrency(m.ganancia, moneda)
                ))
                .collect(Collectors.toList());
        reporte.setMenosVendidos(menosVendidos);

        // 4. Productividad por producto
        List<ProductividadProductoDto> productividadList = listaMetricas.stream()
                .sorted((a, b) -> b.ganancia.compareTo(a.ganancia))
                .map(m -> {
                    BigDecimal margen = BigDecimal.ZERO;
                    if (m.facturacion.compareTo(BigDecimal.ZERO) > 0) {
                        margen = m.ganancia.divide(m.facturacion, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
                    }
                    return new ProductividadProductoDto(
                            m.productoId,
                            m.nombre,
                            m.categoria,
                            m.cantidadVendida,
                            currencyService.roundCurrency(m.facturacion, moneda),
                            currencyService.roundCurrency(m.costos, moneda),
                            currencyService.roundCurrency(m.ganancia, moneda),
                            margen
                    );
                })
                .collect(Collectors.toList());
        reporte.setProductividadProductos(productividadList);

        // 5. Impuestos cobrados
        List<ImpuestoCobradoDto> impuestosList = impuestoMap.values().stream()
                .map(i -> new ImpuestoCobradoDto(
                        i.categoriaFiscal,
                        i.tarifa,
                        currencyService.roundCurrency(i.base, moneda),
                        currencyService.roundCurrency(i.monto, moneda),
                        moneda
                ))
                .collect(Collectors.toList());
        reporte.setImpuestosCobrados(impuestosList);

        // 6. Pérdidas y Bajas (Movimientos PERDIDA, DANADO, AJUSTE_NEGATIVO)
        List<MovimientoInventario> bajas = movimientoRepository.findPerdidasYBajas(negocioId, inicio, fin);
        PerdidasReportDto perdidasDto = new PerdidasReportDto();
        perdidasDto.setMoneda(moneda);

        BigDecimal sumUnidadesBajas = BigDecimal.ZERO;
        BigDecimal sumCostoBajas = BigDecimal.ZERO;
        List<PerdidaItemDto> bajaItems = new ArrayList<>();

        for (MovimientoInventario m : bajas) {
            BigDecimal tasaConversion = currencyService.getEffectiveRate(negocio.getMonedaBase(), moneda, null);

            BigDecimal cant = m.getCantidad() != null ? m.getCantidad() : BigDecimal.ZERO;
            BigDecimal cRef = (m.getCostoReferencia() != null ? m.getCostoReferencia() : BigDecimal.ZERO).multiply(tasaConversion);
            BigDecimal totalCostoMov = cant.multiply(cRef);

            sumUnidadesBajas = sumUnidadesBajas.add(cant);
            sumCostoBajas = sumCostoBajas.add(totalCostoMov);

            PerdidaItemDto pItem = new PerdidaItemDto();
            pItem.setMovimientoId(m.getId());
            if (m.getProducto() != null) {
                pItem.setProductoId(m.getProducto().getId());
                pItem.setProductoNombre(m.getProducto().getNombre());
            }
            pItem.setTipoMovimiento(m.getTipoMovimiento());
            pItem.setCantidad(cant);
            pItem.setCostoReferencia(currencyService.roundCurrency(cRef, moneda));
            pItem.setCostoTotalPerdido(currencyService.roundCurrency(totalCostoMov, moneda));
            pItem.setMotivo(m.getMotivo());
            pItem.setFecha(m.getFechaMovimiento());
            bajaItems.add(pItem);
        }

        perdidasDto.setTotalUnidadesPerdidas(sumUnidadesBajas);
        perdidasDto.setCostoTotalPerdidas(currencyService.roundCurrency(sumCostoBajas, moneda));
        perdidasDto.setDetalles(bajaItems);
        reporte.setPerdidasYBajas(perdidasDto);

        return reporte;
    }

    private static class ImpuestoAcumulado {
        String categoriaFiscal;
        BigDecimal tarifa;
        BigDecimal base = BigDecimal.ZERO;
        BigDecimal monto = BigDecimal.ZERO;

        ImpuestoAcumulado(String cat, BigDecimal tarifa) {
            this.categoriaFiscal = cat;
            this.tarifa = tarifa;
        }
    }

    private static class MetricaProducto {
        UUID productoId;
        String nombre;
        String categoria;
        BigDecimal cantidadVendida = BigDecimal.ZERO;
        BigDecimal facturacion = BigDecimal.ZERO;
        BigDecimal costos = BigDecimal.ZERO;
        BigDecimal ganancia = BigDecimal.ZERO;

        MetricaProducto(UUID id, String nom, String cat) {
            this.productoId = id;
            this.nombre = nom;
            this.categoria = cat;
        }
    }
}
