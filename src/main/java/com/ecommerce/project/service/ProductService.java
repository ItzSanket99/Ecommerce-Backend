package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.ProductRepository;

public interface ProductService {




    ProductDTO addProduct(Product product, Long categoryId);
    ProductResponse getAllProduct();
    ProductResponse searchByCategory(Long categoryId);
    ProductResponse searchByKeyword(String keyword);
    ProductDTO updateProduct(Product product,Long productId);

    ProductDTO deleteProduct(Long productId);
}
