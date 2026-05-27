package com.mall.repository;

import com.mall.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(String role);
}

public interface VillageRepository extends JpaRepository<Village, Long> {}

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    List<Parcel> findByVillageId(Long villageId);
}

public interface HouseholdRepository extends JpaRepository<Household, Long> {
    List<Household> findByVillageId(Long villageId);
    @Query("SELECT COUNT(h) FROM Household h WHERE h.poverty != '一般户'")
    long countPoor();
}

public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByHouseholdId(Long householdId);
}

public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByParcelId(Long parcelId);
}

public interface PolicyRepository extends JpaRepository<Policy, Long> {}

public interface SysConfigRepository extends JpaRepository<SysConfig, Long> {}

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByUserId(Long userId);
    List<Merchant> findByStatus(Integer status);
}

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
}

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMerchantId(Long merchantId);
    List<Product> findByStatus(Integer status);
    List<Product> findByNameContaining(String keyword);
}

public interface SkuRepository extends JpaRepository<Sku, Long> {
    List<Sku> findByProductId(Long productId);
}

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    Optional<Cart> findByUserIdAndSkuId(Long userId, Long skuId);
    void deleteByUserIdAndSkuId(Long userId, Long skuId);
}

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllByOrderByCreatedAtDesc();
}

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
}
