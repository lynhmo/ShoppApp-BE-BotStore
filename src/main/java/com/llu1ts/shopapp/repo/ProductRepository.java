package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByIsDeleted(Boolean isDeleted, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false ORDER BY p.createAt DESC")
    Page<Product> selectNewProduct(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false and  p.category.id = :#{#categoryID}  ORDER BY p.createAt DESC")
    Page<Product> selectSameCategory(@Param("categoryID") Long category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false ORDER BY p.price ASC")
    Page<Product> selectCheapProduct(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchFuzzyByName(@Param("keyword") String keyword);
}