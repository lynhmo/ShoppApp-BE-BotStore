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
import com.llu1ts.shopapp.repo.OrderDetailRepository;
import com.llu1ts.shopapp.repo.ProductImageRepository;
import com.llu1ts.shopapp.repo.ProductRepository;
import com.llu1ts.shopapp.response.ProductRes;
import com.llu1ts.shopapp.response.ProductResponseImage;
import com.llu1ts.shopapp.service.svc.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImpl implements ProductService {


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<ProductResponseImage> getCheapProduct() {
        Pageable top4 = PageRequest.of(0, 4);
        List<Product> products = productRepository.selectCheapProduct(top4).getContent();
        List<ProductResponseImage> productResList = new ArrayList<>();
        for (Product product : products) {
            ProductResponseImage productRes = modelMapper.map(product, ProductResponseImage.class);
            String image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(product.getThumbnail());
            productRes.setCategoryId(product.getCategory().getId());
            productRes.setThumbnail(image);
            productResList.add(productRes);
        }
        return productResList;
    }

    @Override
    public List<ProductResponseImage> getPopularProducts() {
        Pageable top4 = PageRequest.of(0, 4);
        Page<Object[]> products = orderDetailRepository.getTotalQuantity(top4);
        List<ProductResponseImage> productResponseImages = new ArrayList<>();
        products.getContent().forEach(result ->{
            Integer id = (Integer) result[0];
            Long productID = id.longValue();
            Optional<Product> product = productRepository.findById(productID);
            if (product.isPresent()) {
                ProductResponseImage productRes = modelMapper.map(product.get(), ProductResponseImage.class);
                String image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(product.get().getThumbnail());
                productRes.setThumbnail(image);
                productRes.setCategoryId(product.get().getCategory().getId());
                productResponseImages.add(productRes);
            }
        });
        return productResponseImages;
    }

    @Override
    public List<ProductResponseImage> getNewest() {
        Pageable top8 = PageRequest.of(0, 4);
        List<Product> products = productRepository.selectNewProduct(top8).getContent();
        List<ProductResponseImage> productResList = new ArrayList<>();
        for (Product product : products) {
            ProductResponseImage productRes = modelMapper.map(product, ProductResponseImage.class);
            String image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(product.getThumbnail());
            productRes.setCategoryId(product.getCategory().getId());
            productRes.setThumbnail(image);
            productResList.add(productRes);
        }
        return productResList;
    }



    @Override
    @Cacheable(value = "productSearchCache", key = "#query.trim().toLowerCase()")
    public List<ProductRes> searchFuzzyProduct(String query) {
       List<Product> productList = productRepository.searchFuzzyByName(query);
       List<ProductRes> productResList = new ArrayList<>();
       for (Product product : productList) {
           ProductRes productRes = modelMapper.map(product, ProductRes.class);
           productResList.add(productRes);
       }
       return productResList;
    }

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
    public ProductRes createV2(ProductDTO dto) throws DataNotFoundException, IOException {
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
        byte[] thumbnailBytes = dto.getThumbnail().getBytes();
        newProduct.setThumbnail(thumbnailBytes);
        return modelMapper.map(productRepository.save(newProduct), ProductRes.class);
    }

    @Override
    @Cacheable(value = "productCache", key = "#id")
    public ProductRes getProductById(long id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot found product with id: " + id));
        return modelMapper.map(product, ProductRes.class);
    }

    @Override
    public ApiPageResponse<ProductRes> getAllProducts(PageRequest pageRequest, int disable) {
        ApiPageResponse<ProductRes> returnList = new ApiPageResponse<>();
        Page<Product> products;
        if (disable == 1) {
            products = productRepository.findAll(pageRequest);
        } else {
            products = productRepository.findAllByIsDeleted(false, pageRequest);
        }
        return modelCopy(products, returnList);
    }

    private ApiPageResponse<ProductRes> modelCopy(Page<Product> products, ApiPageResponse<ProductRes> returnList) {
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
    public void deleteManyProduct(List<Long> ids) throws DataNotFoundException {
        for (long id : ids) {
            deleteProduct(id);
        }
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
