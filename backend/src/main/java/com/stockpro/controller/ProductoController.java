package com.stockpro.controller;

import com.stockpro.dto.product.CreateProductoRequest;
import com.stockpro.dto.product.ProductoDto;
import com.stockpro.dto.product.UpdateProductoRequest;
import com.stockpro.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDto>> listProducts(
            @RequestParam(required = false) UUID categoriaId
    ) {
        return ResponseEntity.ok(productoService.listProducts(categoriaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(productoService.getProductById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductoRequest request) {
        try {
            ProductoDto dto = productoService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductoRequest request
    ) {
        try {
            ProductoDto dto = productoService.updateProduct(id, request);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        try {
            productoService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("mensaje", "Producto desactivado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductoDto>> getLowStockProducts() {
        return ResponseEntity.ok(productoService.getLowStockProducts());
    }
}
