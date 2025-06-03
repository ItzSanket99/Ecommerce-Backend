package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.ProductRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {




    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);
    ProductResponse getAllProduct();
    ProductResponse searchByCategory(Long categoryId);
    ProductResponse searchByKeyword(String keyword);
    ProductDTO updateProduct(ProductDTO productDTO,Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
