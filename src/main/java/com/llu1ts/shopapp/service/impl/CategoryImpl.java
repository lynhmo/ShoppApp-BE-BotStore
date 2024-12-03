package com.llu1ts.shopapp.service.impl;

import com.llu1ts.shopapp.dto.CategoryDTO;
import com.llu1ts.shopapp.entity.Category;
import com.llu1ts.shopapp.repo.CategoryRepository;
import com.llu1ts.shopapp.service.svc.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryImpl implements CategoryService {

    private final CategoryRepository repo;

    @Override
    public Category createCategory(CategoryDTO dto) {
        try {
            Category category = new Category();
            BeanUtils.copyProperties(dto, category, "id");
            return repo.save(category);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category getCategoryById(Long id) {
        try {
            return repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Category> getAllCategories(Pageable pageable) {
        return repo.findAll();
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO dto) {
        try {
            Category existCate = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            BeanUtils.copyProperties(dto, existCate, "id");
            return repo.save(existCate);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
