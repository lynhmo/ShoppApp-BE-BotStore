package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.ProductDTO;
import com.llu1ts.shopapp.dto.ProductImageDto;
import com.llu1ts.shopapp.entity.Product;
import com.llu1ts.shopapp.entity.ProductImage;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.ProductRes;
import org.springframework.data.domain.PageRequest;

public interface ProductService {
    Product create(ProductDTO product) throws DataNotFoundException;

    Product getProductById(long id) throws Exception;

    ApiPageResponse<ProductRes> getAllProducts(PageRequest pageRequest);

    ProductRes updateProduct(long id, ProductDTO product) throws DataNotFoundException;

    void deleteProduct(long id);

    boolean existsProductByName(String productName);

    ProductImage createProductImage(long productId, ProductImageDto productImageDto) throws DataNotFoundException;
}
