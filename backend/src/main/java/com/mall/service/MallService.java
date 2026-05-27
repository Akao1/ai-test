package com.mall.service;

import com.mall.entity.*;
import com.mall.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.mall.repository.AllRepositories.*;

@Service
public class MallService {
    private final MerchantRepository merchantRepo;
    private final ProductRepository productRepo;
    private final SkuRepository skuRepo;
    private final CartRepository cartRepo;
    private final OrderRepository orderRepo;
    private final AddressRepository addressRepo;
    private final ReviewRepository reviewRepo;
    private final CategoryRepository categoryRepo;
    private final UserRepository userRepo;

    public MallService(MerchantRepository merchantRepo, ProductRepository productRepo,
                       SkuRepository skuRepo, CartRepository cartRepo,
                       OrderRepository orderRepo, AddressRepository addressRepo,
                       ReviewRepository reviewRepo, CategoryRepository categoryRepo,
                       UserRepository userRepo) {
        this.merchantRepo = merchantRepo;
        this.productRepo = productRepo;
        this.skuRepo = skuRepo;
        this.cartRepo = cartRepo;
        this.orderRepo = orderRepo;
        this.addressRepo = addressRepo;
        this.reviewRepo = reviewRepo;
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
    }

    public List<Map<String, Object>> listMerchants() {
        return merchantRepo.findByStatus(1).stream().map(m -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("id", m.getId()); map.put("shopName", m.getShopName());
            map.put("shopLogo", m.getShopLogo()); map.put("shopDesc", m.getShopDesc());
            map.put("contactPhone", m.getContactPhone()); map.put("contactName", m.getContactName());
            map.put("status", m.getStatus());
            map.put("productCount", productRepo.findByMerchantId(m.getId()).size());
            return map;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> merchantDetail(Long id) {
        var m = merchantRepo.findById(id).orElse(null);
        if (m == null) return null;
        var map = new LinkedHashMap<String, Object>();
        map.put("id", m.getId()); map.put("shopName", m.getShopName());
        map.put("shopLogo", m.getShopLogo()); map.put("shopDesc", m.getShopDesc());
        map.put("contactPhone", m.getContactPhone()); map.put("contactName", m.getContactName());
        map.put("status", m.getStatus());
        map.put("products", productRepo.findByMerchantId(m.getId()).stream()
            .map(this::productToMap).collect(Collectors.toList()));
        var cats = productRepo.findByMerchantId(m.getId()).stream()
            .map(p -> categoryRepo.findById(p.getCategoryId()).orElse(null))
            .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        map.put("categories", cats);
        return map;
    }

    public List<Map<String, Object>> listProducts() {
        return productRepo.findByStatus(1).stream().map(this::productToMap).collect(Collectors.toList());
    }

    public Map<String, Object> productDetail(Long id) {
        var p = productRepo.findById(id).orElse(null);
        if (p == null) return null;
        var map = productToMap(p);
        var m = merchantRepo.findById(p.getMerchantId()).orElse(null);
        if (m != null) { map.put("merchantName", m.getShopName()); map.put("merchantLogo", m.getShopLogo()); }
        map.put("skus", skuRepo.findByProductId(id).stream().map(s -> {
            var sm = new LinkedHashMap<String, Object>();
            sm.put("id", s.getId()); sm.put("attrs", s.getAttrs());
            sm.put("price", s.getPrice()); sm.put("stock", s.getStock());
            sm.put("image", s.getImage());
            return sm;
        }).collect(Collectors.toList()));
        return map;
    }

    public List<Map<String, Object>> cart(Long userId) {
        return cartRepo.findByUserId(userId).stream().map(c -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("id", c.getId()); map.put("userId", c.getUserId());
            map.put("skuId", c.getSkuId()); map.put("quantity", c.getQuantity());
            map.put("selected", c.getSelected());
            var sku = skuRepo.findById(c.getSkuId()).orElse(null);
            if (sku != null) {
                map.put("sku", Map.of("id", sku.getId(), "attrs", sku.getAttrs(),
                    "price", sku.getPrice(), "stock", sku.getStock(), "image", sku.getImage()));
                var p = productRepo.findById(sku.getProductId()).orElse(null);
                if (p != null) {
                    map.put("product", Map.of("id", p.getId(), "name", p.getName(), "image",
                        p.getImages() != null ? p.getImages().split(",")[0] : ""));
                    var m = merchantRepo.findById(p.getMerchantId()).orElse(null);
                    if (m != null) map.put("merchantName", m.getShopName());
                }
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> addCart(Long userId, Long skuId, Integer quantity) {
        var existing = cartRepo.findByUserIdAndSkuId(userId, skuId);
        if (existing.isPresent()) {
            var c = existing.get();
            c.setQuantity(c.getQuantity() + quantity);
            cartRepo.save(c);
        } else {
            var c = new Cart();
            c.setUserId(userId); c.setSkuId(skuId); c.setQuantity(quantity); c.setSelected(true);
            cartRepo.save(c);
        }
        return Map.of("success", true);
    }

    @Transactional
    public Map<String, Object> createOrder(Long userId, List<Long> skuIds, Long addressId, String remark) {
        var addr = addressRepo.findById(addressId).orElse(null);
        if (addr == null || !addr.getUserId().equals(userId)) return Map.of("success", false);
        var cartItems = cartRepo.findByUserId(userId).stream()
            .filter(c -> skuIds.contains(c.getSkuId())).collect(Collectors.toList());
        if (cartItems.isEmpty()) return Map.of("success", false);

        double total = 0;
        var items = new ArrayList<Map<String, Object>>();
        for (var ci : cartItems) {
            var sku = skuRepo.findById(ci.getSkuId()).orElse(null);
            if (sku == null || sku.getStock() < ci.getQuantity()) continue;
            var p = productRepo.findById(sku.getProductId()).orElse(null);
            if (p == null) continue;
            sku.setStock(sku.getStock() - ci.getQuantity());
            skuRepo.save(sku);
            p.setSales(p.getSales() + ci.getQuantity());
            productRepo.save(p);
            double sub = sku.getPrice() * ci.getQuantity();
            total += sub;
            var item = new LinkedHashMap<String, Object>();
            item.put("skuId", sku.getId()); item.put("productId", p.getId());
            item.put("quantity", ci.getQuantity()); item.put("price", sku.getPrice());
            item.put("productName", p.getName()); item.put("productImage",
                p.getImages() != null ? p.getImages().split(",")[0] : "");
            item.put("skuAttrs", sku.getAttrs());
            item.put("merchantId", p.getMerchantId());
            var m = merchantRepo.findById(p.getMerchantId()).orElse(null);
            item.put("merchantName", m != null ? m.getShopName() : "");
            items.add(item);
        }

        var order = new Order();
        order.setUserId(userId);
        order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setTotalAmount(total);
        order.setStatus("待付款");
        order.setRemark(remark);
        order.setAddressSnapshot(addr.getProvince() + addr.getCity() + addr.getDistrict() + addr.getDetail() + " " + addr.getName() + " " + addr.getPhone());
        order.setItems(items.toString());
        order.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        orderRepo.save(order);

        cartRepo.deleteAll(cartItems);

        var result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("order", orderToMap(order));
        return result;
    }

    public Map<String, Object> productToMap(Product p) {
        var map = new LinkedHashMap<String, Object>();
        map.put("id", p.getId()); map.put("merchantId", p.getMerchantId());
        map.put("name", p.getName()); map.put("categoryId", p.getCategoryId());
        map.put("price", p.getPrice()); map.put("originalPrice", p.getOriginalPrice());
        map.put("stock", p.getStock()); map.put("status", p.getStatus());
        map.put("images", p.getImages()); map.put("description", p.getDescription());
        map.put("sales", p.getSales()); map.put("rating", p.getRating());
        map.put("createdAt", p.getCreatedAt());
        var m = merchantRepo.findById(p.getMerchantId()).orElse(null);
        if (m != null) { map.put("merchantName", m.getShopName()); map.put("merchantLogo", m.getShopLogo()); }
        var cat = categoryRepo.findById(p.getCategoryId()).orElse(null);
        if (cat != null) map.put("categoryName", cat.getName());
        return map;
    }

    static Map<String, Object> orderToMap(Order o) {
        var map = new LinkedHashMap<String, Object>();
        map.put("id", o.getId()); map.put("userId", o.getUserId());
        map.put("orderNo", o.getOrderNo()); map.put("totalAmount", o.getTotalAmount());
        map.put("status", o.getStatus()); map.put("remark", o.getRemark());
        map.put("addressSnapshot", o.getAddressSnapshot());
        map.put("items", o.getItems()); map.put("createdAt", o.getCreatedAt());
        return map;
    }

    // Delegated CRUD methods for controllers
    public Merchant saveMerchant(Merchant m) { return merchantRepo.save(m); }
    public Optional<Merchant> findMerchant(Long id) { return merchantRepo.findById(id); }
    public Optional<Merchant> findMerchantByUser(Long userId) { return merchantRepo.findByUserId(userId); }
    public List<Merchant> allMerchants() { return merchantRepo.findAll(); }
    public Product saveProduct(Product p) { return productRepo.save(p); }
    public Optional<Product> findProduct(Long id) { return productRepo.findById(id); }
    public List<Product> merchantProducts(Long merchantId) { return productRepo.findByMerchantId(merchantId); }
    public void deleteProduct(Long id) { productRepo.deleteById(id); }
    public List<Product> searchProducts(String keyword) { return productRepo.findByNameContaining(keyword); }
    public Sku saveSku(Sku s) { return skuRepo.save(s); }
    public Optional<Sku> findSku(Long id) { return skuRepo.findById(id); }
    public List<Sku> productSkus(Long productId) { return skuRepo.findByProductId(productId); }
    public void deleteSku(Long id) { skuRepo.deleteById(id); }
    public Cart saveCart(Cart c) { return cartRepo.save(c); }
    public Optional<Cart> findCart(Long id) { return cartRepo.findById(id); }
    public void deleteCart(Long id) { cartRepo.deleteById(id); }
    public Order saveOrder(Order o) { return orderRepo.save(o); }
    public Optional<Order> findOrder(Long id) { return orderRepo.findById(id); }
    public List<Order> userOrders(Long userId) { return orderRepo.findByUserIdOrderByCreatedAtDesc(userId); }
    public List<Order> allOrders() { return orderRepo.findAllByOrderByCreatedAtDesc(); }
    public Address saveAddress(Address a) { return addressRepo.save(a); }
    public Optional<Address> findAddress(Long id) { return addressRepo.findById(id); }
    public List<Address> userAddresses(Long userId) { return addressRepo.findByUserId(userId); }
    public void deleteAddress(Long id) { addressRepo.deleteById(id); }
    public Review saveReview(Review r) { return reviewRepo.save(r); }
    public List<Review> productReviews(Long productId) { return reviewRepo.findByProductId(productId); }
    public Category saveCategory(Category c) { return categoryRepo.save(c); }
    public List<Category> allCategories() { return categoryRepo.findAll(); }
    public Optional<Category> findCategory(Long id) { return categoryRepo.findById(id); }
    public void deleteCategory(Long id) { categoryRepo.deleteById(id); }
}
