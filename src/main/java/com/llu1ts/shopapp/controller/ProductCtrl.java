package com.llu1ts.shopapp.controller;


import com.github.javafaker.Faker;
import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.common.ImageUpload;
import com.llu1ts.shopapp.dto.DeleteManyDto;
import com.llu1ts.shopapp.dto.ProductDTO;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.ProductRes;
import com.llu1ts.shopapp.response.SuccessResponse;
import com.llu1ts.shopapp.service.svc.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductCtrl {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiPageResponse<ProductRes>> getAllProduct(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "0") int disable
    ) {
        return ResponseEntity.ok(productService.getAllProducts(PageRequest.of(page, size), disable));
    }

    @GetMapping("/{prodId}")
    public ResponseEntity<ProductRes> getProduct(@PathVariable int prodId) throws Exception {
        return ResponseEntity.ok(productService.getProductById(prodId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductRes>> searchProduct(@RequestParam String query) throws Exception {
        return ResponseEntity.ok(productService.searchFuzzyProduct(query));
    }

    @PostMapping
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult bindingResult) throws DataNotFoundException {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.toString());
        }
        return ResponseEntity.ok(productService.create(productDTO));
    }


    @PostMapping(value = "/v2")
    public ResponseEntity<?> insertProductWithImage(@Valid @ModelAttribute ProductDTO productDTO,
                                                    BindingResult bindingResult) throws DataNotFoundException, IOException {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.toString());
        }
        return ResponseEntity.ok(productService.createV2(productDTO));
    }

    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> insertProductImage(@PathVariable(value = "id") long productId,
                                                @ModelAttribute ImageUpload imageUpload) throws Exception {
        return ResponseEntity.ok(productService.createProductImage(productId, imageUpload));
    }

    @GetMapping("/images/{prodId}")
    public ResponseEntity<ProductRes> getProduct(@PathVariable long prodId) throws Exception {
        return ResponseEntity.ok(productService.getProductById(prodId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductRes> updateProduct(@PathVariable long id, @RequestBody ProductDTO product) throws DataNotFoundException {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteProduct(@PathVariable int id) throws DataNotFoundException {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new SuccessResponse("Deleted successfully", "200", null));
    }

    @PostMapping("/delete-many")
    public ResponseEntity<SuccessResponse> deleteManyProduct(@RequestBody DeleteManyDto dto) throws DataNotFoundException {
        productService.deleteManyProduct(dto.getIds());
        return ResponseEntity.ok(new SuccessResponse("Deleted many successfully", "200", null));
    }


    @PostMapping("/fake")
    public ResponseEntity<?> fake() throws DataNotFoundException {
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            String name = faker.commerce().productName();
            if (productService.existsProductByName(name)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already exists");
            }
            productService.create(ProductDTO.builder()
                    .name(name)
                    .price((float) faker.number().numberBetween(1000, 1000000))
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(3, 6))
                    .build());
        }


        return ResponseEntity.ok("Faked");
    }
}
