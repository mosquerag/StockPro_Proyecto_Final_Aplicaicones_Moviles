package com.stockpro.service;

import com.stockpro.dto.category.CategoriaDto;
import com.stockpro.dto.category.CreateCategoriaRequest;
import com.stockpro.model.entity.Categoria;
import com.stockpro.model.entity.Negocio;
import com.stockpro.repository.CategoriaRepository;
import com.stockpro.repository.NegocioRepository;
import com.stockpro.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final NegocioRepository negocioRepository;
    private final SecurityUtils securityUtils;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            NegocioRepository negocioRepository,
            SecurityUtils securityUtils
    ) {
        this.categoriaRepository = categoriaRepository;
        this.negocioRepository = negocioRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDto> listCategories() {
        UUID negocioId = securityUtils.getCurrentNegocioId();
        return categoriaRepository.findByNegocioIdAndActivoTrueOrderByNombreAsc(negocioId)
                .stream()
                .map(CategoriaDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaDto createCategory(CreateCategoriaRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        categoriaRepository.findByNegocioIdAndNombreIgnoreCase(negocioId, request.getNombre().trim())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + request.getNombre() + "' en este negocio");
                });

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalStateException("Negocio no encontrado"));

        Categoria categoria = new Categoria();
        categoria.setNegocio(negocio);
        categoria.setNombre(request.getNombre().trim());
        categoria.setDescripcion(request.getDescripcion());
        if (request.getColorHex() != null && !request.getColorHex().isBlank()) {
            categoria.setColorHex(request.getColorHex().trim());
        }
        categoria.setActivo(true);

        return new CategoriaDto(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaDto updateCategory(UUID id, CreateCategoriaRequest request) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Categoria categoria = categoriaRepository.findById(id)
                .filter(c -> c.getNegocio().getId().equals(negocioId))
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada para el negocio actual"));

        categoria.setNombre(request.getNombre().trim());
        categoria.setDescripcion(request.getDescripcion());
        if (request.getColorHex() != null && !request.getColorHex().isBlank()) {
            categoria.setColorHex(request.getColorHex().trim());
        }

        return new CategoriaDto(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deleteCategory(UUID id) {
        UUID negocioId = securityUtils.getCurrentNegocioId();

        Categoria categoria = categoriaRepository.findById(id)
                .filter(c -> c.getNegocio().getId().equals(negocioId))
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada para el negocio actual"));

        // Baja lógica
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }
}
