package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.category.CategoryResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Categorias", description = "Categorias de transações (receitas e despesas)")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Listar categorias", description = "Lista todas as categorias do usuário")
    @ApiResponse(responseCode = "200", description = "Lista de categorias")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var categories = categoryService.findAllByUserId(user.getId());
        
        List<CategoryResponse> response = categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getIcon(),
                        category.getType().name()
                ))
                .toList();
        
        return ResponseEntity.ok(response);
    }
}
