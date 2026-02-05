package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.CategoryResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

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
