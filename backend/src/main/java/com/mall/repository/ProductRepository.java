package com.mall.repository;

import com.mall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMerchantId(Long merchantId);
    List<Product> findByStatus(Integer status);
    List<Product> findByNameContaining(String keyword);
}
