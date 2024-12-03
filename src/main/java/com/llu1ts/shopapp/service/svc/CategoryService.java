package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.dto.CategoryDTO;
import com.llu1ts.shopapp.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryDTO category);

    Category getCategoryById(Long id);

    List<Category> getAllCategories(Pageable pageable);

    Category updateCategory(Long id, CategoryDTO category);

    void deleteCategory(Long id);
}
