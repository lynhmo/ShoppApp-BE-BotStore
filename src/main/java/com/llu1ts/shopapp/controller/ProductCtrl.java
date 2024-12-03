package com.llu1ts.shopapp.controller;


import com.github.javafaker.Faker;
import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.common.ImageUpload;
import com.llu1ts.shopapp.dto.ProductDTO;
import com.llu1ts.shopapp.dto.ProductImageDto;
import com.llu1ts.shopapp.entity.Product;
import com.llu1ts.shopapp.entity.ProductImage;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.ProductRes;
import com.llu1ts.shopapp.service.svc.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductCtrl {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiPageResponse<ProductRes>> getAllProduct(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProducts(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRes> getProduct(@PathVariable int id) throws Exception {
        ProductRes productRes = new ProductRes();
        Product existProd = productService.getProductById(id);
        return ResponseEntity.ok(ProductRes.fromProduct(existProd,productRes));
    }

    @PostMapping
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errors.toString());
            }
            return ResponseEntity.ok(productService.create(productDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> insertProductImage(@PathVariable(value = "id") long productId,
                                                @ModelAttribute ImageUpload imageUpload) throws Exception {
        try {
            Product existingProduct = productService.getProductById(productId);
            List<ProductImage> productImages = new ArrayList<>();
            //File
            List<MultipartFile> filesList = imageUpload.getFiles() == null ? new ArrayList<>() : imageUpload.getFiles();
            if (filesList.size()>=5){
                return ResponseEntity.badRequest().body("Qua5anh");
            }
            for (MultipartFile file : filesList) {
                //Check nếu có files mà không có content bên trong == 0
                if (file.getSize() == 0) {
                    continue;
                }
                // Check file size có lơn hơn 10MB
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File too large");
                }
                // Lấy ra content type và check xem có phải ảnh không
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Unsupported Media Type");
                }

                // file name được lưu vào bang product_images
                String fileName = storeFile(file);

                ProductImage newProductImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDto.builder().imageUrl(fileName).build());
                productImages.add(newProductImage);
            }
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID và file name
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        // Đường dẫn lưu file
        Path path = Paths.get("upload");
        // Check xem file có tồn tại không
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        // Đường dẫn đầy đủ đến file
        Path destination = Paths.get(path.toString(), uniqueFileName);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable long id,@RequestBody ProductDTO product) throws DataNotFoundException {
        return ResponseEntity.ok(productService.updateProduct(id,  product));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted successfully");
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
