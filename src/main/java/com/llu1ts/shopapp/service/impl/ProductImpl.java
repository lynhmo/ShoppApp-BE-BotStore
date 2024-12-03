package com.llu1ts.shopapp.service.impl;


import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.ProductDTO;
import com.llu1ts.shopapp.dto.ProductImageDto;
import com.llu1ts.shopapp.entity.Category;
import com.llu1ts.shopapp.entity.Product;
import com.llu1ts.shopapp.entity.ProductImage;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.exception.InvalidParamException;
import com.llu1ts.shopapp.repo.CategoryRepository;
import com.llu1ts.shopapp.repo.ProductImageRepository;
import com.llu1ts.shopapp.repo.ProductRepository;
import com.llu1ts.shopapp.response.ProductRes;
import com.llu1ts.shopapp.service.svc.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImpl implements ProductService {


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;


    @Override
    public Product create(ProductDTO dto) throws DataNotFoundException {
        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException("Category not found with id: " + dto.getCategoryId()));
        Product newProduct = new Product();
        BeanUtils.copyProperties(dto, newProduct);
        newProduct.setCategory(category);
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws Exception {
        return productRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot found product with id: " + id));
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
                .collect(Collectors.toList());
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
    public void deleteProduct(long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        productOptional.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsProductByName(String productName) {
        return productRepository.existsByName(productName);
    }

    @Override
    public ProductImage createProductImage(long productId, ProductImageDto productImageDto) throws DataNotFoundException {
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
}
