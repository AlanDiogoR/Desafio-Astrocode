package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllByUserId(UUID userId) {
        return categoryRepository.findByUserId(userId);
    }
}
