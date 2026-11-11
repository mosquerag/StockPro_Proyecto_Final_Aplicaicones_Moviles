package com.stockpro.service;

import com.stockpro.dto.product.CreateProductoRequest;
import com.stockpro.dto.product.ProductoDto;
import com.stockpro.dto.product.UpdateProductoRequest;
import com.stockpro.model.entity.*;
import com.stockpro.repository.*;
import com.stockpro.exception.*;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final TarifaImpuestoRepository tarifaImpuestoRepository;
    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final LoteRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final SecurityUtils securityUtils;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            TarifaImpuestoRepository tarifaImpuestoRepository,
            NegocioRepository negocioRepository,
            UsuarioRepository usuarioRepository,
            LoteRepository loteRepository,
            MovimientoInventarioRepository movimientoRepository,
            SecurityUtils securityUtils
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.tarifaImpuestoRepository = tarifaImpuestoRepository;
        this.negocioRepository = negocioRepository;
        this.usuarioRepository = usuarioRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> listProducts(UUID categoriaId) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        List<Producto> productos = productoRepository.findByNegocioIdAndActivoTrueOrderByNombreAsc(negocioId);

        if (categoriaId != null) {
            productos = productos.stream()
                    .filter(p -> p.getCategoria() != null && p.getCategoria().getId().equals(categoriaId))
                    .collect(Collectors.toList());
        }

        return productos.stream().map(ProductoDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDto getProductById(UUID id) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Producto producto = productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("PRODUCTO_NO_ENCONTRADO", "Producto no encontrado o no pertenece a su negocio"));

        return new ProductoDto(producto);
    }

    @Transactional
    public ProductoDto createProduct(CreateProductoRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        UUID usuarioId = securityUtils.getCurrentUsuarioId();

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalStateException("Negocio no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .filter(c -> c.getNegocio().getId().equals(negocioId))
                .orElseThrow(() -> new IllegalArgumentException("Categoría inválida para este negocio"));

        Producto producto = new Producto();
        producto.setNegocio(negocio);
        producto.setCategoria(categoria);
        producto.setNombre(request.getNombre().trim());
        producto.setCodigoBarras(request.getCodigoBarras());
        producto.setUnidadMedida(request.getUnidadMedida() != null ? request.getUnidadMedida() : "UNIDAD");
        producto.setCosto(request.getCosto());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setCategoriaFiscal(request.getCategoriaFiscal());
        producto.setPrecioIncluyeImpuesto(request.getPrecioIncluyeImpuesto() != null ? request.getPrecioIncluyeImpuesto() : true);
        producto.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : new BigDecimal("5.00"));
        producto.setManejaLotes(request.getManejaLotes() != null ? request.getManejaLotes() : false);
        producto.setImagenUrl(request.getImagenUrl());
        producto.setActivo(true);
        producto.setStockActual(BigDecimal.ZERO); // Inicialmente cero, se altera solo por movimiento transaccional

        // Resolver tarifa de impuesto 2026 correspondiente al país y categoría fiscal
        TarifaImpuesto tarifa = resolverTarifaImpuesto(request.getTarifaImpuestoId(), negocio.getPais(), request.getCategoriaFiscal());
        if (tarifa != null) {
            producto.setTarifaImpuesto(tarifa);
            producto.setPorcentajeImpuestoAplicado(tarifa.getPorcentaje());
        }

        producto = productoRepository.save(producto);

        // Si se especificó inventario inicial, se ingresa mediante movimiento trazable y lote si aplica
        if (request.getCantidadInicial() != null && request.getCantidadInicial().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal cantidadInicial = request.getCantidadInicial();
            Lote loteCreado = null;

            if (Boolean.TRUE.equals(producto.getManejaLotes())) {
                String numLote = (request.getNumeroLoteInicial() != null && !request.getNumeroLoteInicial().isBlank())
                        ? request.getNumeroLoteInicial().trim()
                        : "LOTE-INI-" + System.currentTimeMillis();

                Lote lote = new Lote();
                lote.setNegocio(negocio);
                lote.setProducto(producto);
                lote.setNumeroLote(numLote);
                lote.setCantidadInicial(cantidadInicial);
                lote.setCantidadDisponible(cantidadInicial);
                lote.setFechaVencimiento(request.getFechaVencimientoInicial());
                lote.setEstado("ACTIVO");
                loteCreado = loteRepository.save(lote);
            }

            // Registrar movimiento de inventario obligatorio (única forma de alterar stock)
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setNegocio(negocio);
            movimiento.setProducto(producto);
            movimiento.setLote(loteCreado);
            movimiento.setTipoMovimiento("ENTRADA");
            movimiento.setCantidad(cantidadInicial);
            movimiento.setStockAnterior(BigDecimal.ZERO);
            movimiento.setStockPosterior(cantidadInicial);
            movimiento.setCostoReferencia(producto.getCosto());
            movimiento.setMotivo("Ingreso de inventario inicial al registrar producto");
            movimiento.setUsuario(usuario);
            movimiento.setReferenciaTipo("ALTA_INICIAL");
            movimientoRepository.save(movimiento);

            producto.setStockActual(cantidadInicial);
            producto = productoRepository.save(producto);
        }

        return new ProductoDto(producto);
    }

    @Transactional
    public ProductoDto updateProduct(UUID id, UpdateProductoRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Producto producto = productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o no pertenece a su negocio"));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .filter(c -> c.getNegocio().getId().equals(negocioId))
                .orElseThrow(() -> new IllegalArgumentException("Categoría inválida para este negocio"));

        producto.setCategoria(categoria);
        producto.setNombre(request.getNombre().trim());
        producto.setCodigoBarras(request.getCodigoBarras());
        if (request.getUnidadMedida() != null) producto.setUnidadMedida(request.getUnidadMedida());
        producto.setCosto(request.getCosto());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setCategoriaFiscal(request.getCategoriaFiscal());
        if (request.getPrecioIncluyeImpuesto() != null) producto.setPrecioIncluyeImpuesto(request.getPrecioIncluyeImpuesto());
        if (request.getStockMinimo() != null) producto.setStockMinimo(request.getStockMinimo());
        if (request.getManejaLotes() != null) producto.setManejaLotes(request.getManejaLotes());
        if (request.getImagenUrl() != null) producto.setImagenUrl(request.getImagenUrl());
        if (request.getActivo() != null) producto.setActivo(request.getActivo());

        // Actualizar tarifa de impuesto si cambió
        TarifaImpuesto tarifa = resolverTarifaImpuesto(request.getTarifaImpuestoId(), producto.getNegocio().getPais(), request.getCategoriaFiscal());
        if (tarifa != null) {
            producto.setTarifaImpuesto(tarifa);
            producto.setPorcentajeImpuestoAplicado(tarifa.getPorcentaje());
        }

        // REGLA FUNDAMENTAL:
        // El stockActual NUNCA se modifica en esta operación. Solo se cambia vía MovimientoInventario.
        return new ProductoDto(productoRepository.save(producto));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Producto producto = productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o no pertenece a su negocio"));

        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getLowStockProducts() {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        return productoRepository.findStockBajo(negocioId).stream()
                .map(ProductoDto::new)
                .collect(Collectors.toList());
    }

    private TarifaImpuesto resolverTarifaImpuesto(UUID tarifaId, String pais, String categoriaFiscal) {
        if (tarifaId != null) {
            return tarifaImpuestoRepository.findById(tarifaId).orElse(null);
        }
        return tarifaImpuestoRepository.findByPaisAndCategoriaFiscalAndActivoTrue(pais, categoriaFiscal)
                .orElse(null);
    }
}
