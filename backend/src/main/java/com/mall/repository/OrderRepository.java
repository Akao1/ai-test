package com.mall.repository;

import com.mall.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllByOrderByCreatedAtDesc();
}
