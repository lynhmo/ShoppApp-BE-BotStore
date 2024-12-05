package com.llu1ts.shopapp.service.impl;


import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.common.ImageUpload;
import com.llu1ts.shopapp.dto.ProductDTO;
import com.llu1ts.shopapp.dto.ProductImageDto;
import com.llu1ts.shopapp.entity.Category;
import com.llu1ts.shopapp.entity.Product;
import com.llu1ts.shopapp.entity.ProductImage;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.exception.ExceedDataException;
import com.llu1ts.shopapp.exception.InvalidParamException;
import com.llu1ts.shopapp.repo.CategoryRepository;
import com.llu1ts.shopapp.repo.ProductImageRepository;
import com.llu1ts.shopapp.repo.ProductRepository;
import com.llu1ts.shopapp.response.ProductRes;
import com.llu1ts.shopapp.service.svc.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImpl implements ProductService {


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;


    @Override
    public ProductRes create(ProductDTO dto) throws DataNotFoundException {
        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException("Category not found with id: " + dto.getCategoryId()));
        if (Boolean.TRUE.equals(category.getIsDeleted())) {
            throw new IllegalArgumentException("Category is deleted");
        }
        Product newProduct = new Product();
        BeanUtils.copyProperties(dto, newProduct);
        newProduct.setCategory(category);
        return modelMapper.map(productRepository.save(newProduct), ProductRes.class);
    }

    @Override
    public ProductRes getProductById(long id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot found product with id: " + id));
        return modelMapper.map(product, ProductRes.class);
    }

    @Override
    public ApiPageResponse<ProductRes> getAllProducts(PageRequest pageRequest) {
        ApiPageResponse<ProductRes> returnList = new ApiPageResponse<>();
        Page<Product> products = productRepository.findAll(pageRequest);
        List<ProductRes> productList = products.getContent().stream()
                .map(product -> {
                    ProductRes newProd = new ProductRes();
                    try {
                        BeanUtils.copyProperties(product, newProd);
                        newProd.setCategoryId(product.getCategory().getId());
                    } catch (BeansException e) {
                        e.printStackTrace();
                    }
                    return newProd;
                })
                .toList();
        BeanUtils.copyProperties(products, returnList);
        returnList.setContent(productList);
        return returnList;
    }

    @Override
    public ProductRes updateProduct(long id, ProductDTO product) throws DataNotFoundException {
        Product existingProduct = productRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot found product with id: " + id));
        Category category = categoryRepository
                .findById(product.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException("Category not found with id: " + product.getCategoryId()));
        BeanUtils.copyProperties(product, existingProduct);
        existingProduct.setCategory(category);
        productRepository.save(existingProduct);
        ProductRes res = new ProductRes();
        return ProductRes.fromProduct(existingProduct, res);
    }

    @Override
    public void deleteProduct(long id) throws DataNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Product not found"));
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Override
    public boolean existsProductByName(String productName) {
        return productRepository.existsByName(productName);
    }

    @Override
    public List<ProductImage> createProductImage(long productId, ImageUpload imageUpload) throws DataNotFoundException, IOException {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() ->
                new DataNotFoundException("Product not found"));
        List<ProductImage> productImages = new ArrayList<>();
        //File
        List<MultipartFile> filesList = imageUpload.getFiles() == null ? new ArrayList<>() : imageUpload.getFiles();
        if (filesList.size() >= 5) {
            throw new ExceedDataException("Exceed 5 files");
        }
        for (MultipartFile file : filesList) {
            //Check nếu có files mà không có content bên trong == 0
            if (file.getSize() == 0) {
                continue;
            }
            // Check file size có lơn hơn 10MB
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IOException("File too large");
            }
            // Lấy ra content type và check xem có phải ảnh không
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IOException("Unsupported Media Type");
            }

            // file name được lưu vào bang product_images
            String fileName = storeFile(file);

            ProductImage newProductImage = getProductImage(existingProduct.getId(),
                    ProductImageDto.builder().imageUrl(fileName).build());
            productImages.add(newProductImage);
        }


        return productImages;
    }

    private ProductImage getProductImage(long productId, ProductImageDto productImageDto) throws DataNotFoundException {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find product with id: " + productImageDto.getProductId()));
        ProductImage productImage = new ProductImage();
        BeanUtils.copyProperties(productImageDto, productImage);
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= 5) {
            throw new InvalidParamException("Number of images exceeds 5!");
        }
        productImage.setProduct(existingProduct);
        return productImageRepository.save(productImage);
    }


    private String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID và file name
        String uniqueFileName = UUID.randomUUID() + "_" + fileName;
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
}
