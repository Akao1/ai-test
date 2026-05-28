package com.mall.repository;

import com.mall.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    Optional<Cart> findByUserIdAndSkuId(Long userId, Long skuId);
    void deleteByUserIdAndSkuId(Long userId, Long skuId);
}
