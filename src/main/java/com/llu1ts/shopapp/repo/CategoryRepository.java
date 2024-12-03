package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsById(Long id);
}