package com.llu1ts.shopapp.controller;

import com.llu1ts.shopapp.dto.CategoryDTO;
import com.llu1ts.shopapp.entity.Category;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.SuccessResponse;
import com.llu1ts.shopapp.service.svc.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryCtrl {

    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategorie(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.getAllCategories(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable long id) {
        try {
            return ResponseEntity.ok(service.getCategoryById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryDTO category, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errors.toString());
            }
            return ResponseEntity.ok(service.createCategory(category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable long id, @RequestBody @Valid CategoryDTO category, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errors.toString());
            }
            return ResponseEntity.ok(service.updateCategory(id, category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteCategory(@PathVariable long id) throws DataNotFoundException {
        service.deleteCategory(id);
        return ResponseEntity.ok(new SuccessResponse("Deleted success fully", "200", null));
    }
}
