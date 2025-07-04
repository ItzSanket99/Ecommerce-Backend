package com.ecommerce.project.repositories;

import com.ecommerce.project.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItems,Long> {
}
