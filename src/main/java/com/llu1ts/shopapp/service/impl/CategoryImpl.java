package com.llu1ts.shopapp.service.impl;

import com.llu1ts.shopapp.dto.CategoryDTO;
import com.llu1ts.shopapp.entity.Category;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.repo.CategoryRepository;
import com.llu1ts.shopapp.service.svc.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category, "id");
        return repo.save(category);
    }

    @Override
    public Category getCategoryById(Long id) throws DataNotFoundException {
        return repo.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

    }

    @Override
    public List<Category> getAllCategories(Pageable pageable) {
        return repo.findAll();
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO dto) throws DataNotFoundException {
        Category existCate = repo.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        BeanUtils.copyProperties(dto, existCate, "id");
        return repo.save(existCate);

    }

    @Override
    public void deleteCategory(Long id) throws DataNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Category not found"));
        category.setIsDeleted(true);
        repo.save(category);
    }
}
