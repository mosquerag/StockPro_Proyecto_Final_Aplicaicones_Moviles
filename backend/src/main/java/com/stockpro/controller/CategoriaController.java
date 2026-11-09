package com.stockpro.controller;

import com.stockpro.dto.category.CategoriaDto;
import com.stockpro.dto.category.CreateCategoriaRequest;
import com.stockpro.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDto>> listCategories() {
        return ResponseEntity.ok(categoriaService.listCategories());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CreateCategoriaRequest request) {
        try {
            CategoriaDto dto = categoriaService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID id, @Valid @RequestBody CreateCategoriaRequest request) {
        try {
            CategoriaDto dto = categoriaService.updateCategory(id, request);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID id) {
        try {
            categoriaService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("mensaje", "Categoría desactivada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
