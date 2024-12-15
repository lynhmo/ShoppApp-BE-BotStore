package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.common.ImageUpload;
import com.llu1ts.shopapp.dto.ProductDTO;
import com.llu1ts.shopapp.entity.ProductImage;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.ProductRes;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductRes create(ProductDTO product) throws DataNotFoundException;

    ProductRes createV2(ProductDTO product) throws DataNotFoundException, IOException;

    ProductRes getProductById(long id) throws Exception;

    ApiPageResponse<ProductRes> getAllProducts(PageRequest pageRequest, int disable);

    ProductRes updateProduct(long id, ProductDTO product) throws DataNotFoundException;

    void deleteProduct(long id) throws DataNotFoundException;

    boolean existsProductByName(String productName);

    List<ProductImage> createProductImage(long productId, ImageUpload imageUpload) throws DataNotFoundException, IOException;
}
