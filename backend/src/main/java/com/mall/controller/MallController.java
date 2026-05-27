package com.mall.controller;

import com.mall.entity.*;
import com.mall.service.*;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MallController {

    private final AuthService authService;
    private final MallService mallService;
    private final AiChatService aiChatService;

    public MallController(AuthService authService, MallService mallService, AiChatService aiChatService) {
        this.authService = authService;
        this.mallService = mallService;
        this.aiChatService = aiChatService;
    }

    private Long getUserId(Authentication auth) {
        return Long.parseLong(((Claims) auth.getDetails()).getSubject());
    }

    private String getRole(Authentication auth) {
        return ((Claims) auth.getDetails()).get("role", String.class);
    }

    // --- Categories ---
    @GetMapping("/categories")
    public List<Category> listCategories() { return mallService.allCategories(); }
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return mallService.findCategory(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category c) { return mallService.saveCategory(c); }
    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category c) { c.setId(id); return mallService.saveCategory(c); }
    @DeleteMapping("/categories/{id}")
    public Map<String, Boolean> deleteCategory(@PathVariable Long id) { mallService.deleteCategory(id); return Map.of("success", true); }

    // --- Merchants ---
    @GetMapping("/merchants")
    public List<Map<String, Object>> listMerchants() { return mallService.listMerchants(); }

    @GetMapping("/merchants/{id}")
    public ResponseEntity<Map<String, Object>> getMerchant(@PathVariable Long id) {
        var detail = mallService.merchantDetail(id);
        return detail != null ? ResponseEntity.ok(detail) : ResponseEntity.notFound().build();
    }

    @PostMapping("/merchants/register")
    public Map<String, Object> registerMerchant(Authentication auth, @RequestBody Map<String, String> body) {
        Long userId = getUserId(auth);
        var existing = mallService.findMerchantByUser(userId);
        if (existing.isPresent()) return Map.of("success", false, "message", "已申请过店铺");

        var m = new Merchant();
        m.setUserId(userId); m.setShopName(body.get("shopName"));
        m.setShopLogo(body.getOrDefault("shopLogo", "🏪")); m.setShopDesc(body.getOrDefault("shopDesc", ""));
        m.setContactPhone(body.get("contactPhone")); m.setContactName(body.get("contactName"));
        m.setStatus(0); m.setCreatedAt(java.time.LocalDate.now().toString());
        mallService.saveMerchant(m);
        return Map.of("success", true);
    }

    @PutMapping("/merchants/{id}")
    public Merchant updateMerchant(@PathVariable Long id, @RequestBody Merchant m) { m.setId(id); return mallService.saveMerchant(m); }

    @PutMapping("/merchants/{id}/status")
    public Merchant updateMerchantStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        var m = mallService.findMerchant(id).orElseThrow();
        m.setStatus(body.get("status"));
        return mallService.saveMerchant(m);
    }

    @GetMapping("/admin/merchants")
    public List<Merchant> adminMerchants() { return mallService.allMerchants(); }

    // --- Products ---
    @GetMapping("/products")
    public List<Map<String, Object>> listProducts(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return mallService.searchProducts(keyword).stream()
                .map(mallService::productToMap).collect(Collectors.toList());
        }
        return mallService.listProducts();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        var p = mallService.productDetail(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @GetMapping("/products/merchant/{merchantId}")
    public List<Map<String, Object>> merchantProducts(@PathVariable Long merchantId) {
        return mallService.merchantProducts(merchantId).stream()
            .map(mallService::productToMap).collect(Collectors.toList());
    }

    @PostMapping("/products/add")
    public Product addProduct(Authentication auth, @RequestBody Product p) {
        String role = getRole(auth);
        if (!"merchant".equals(role) && !"admin".equals(role)) throw new RuntimeException("无权限");
        if ("merchant".equals(role)) {
            var m = mallService.findMerchantByUser(getUserId(auth)).orElseThrow();
            p.setMerchantId(m.getId());
        }
        p.setCreatedAt(java.time.LocalDate.now().toString());
        p.setSales(0); p.setRating(0.0);
        return mallService.saveProduct(p);
    }

    @PutMapping("/products/edit/{id}")
    public Product editProduct(@PathVariable Long id, @RequestBody Product p) {
        p.setId(id); return mallService.saveProduct(p);
    }

    @DeleteMapping("/products/del/{id}")
    public Map<String, Boolean> deleteProduct(@PathVariable Long id) {
        mallService.deleteProduct(id);
        return Map.of("success", true);
    }

    // --- SKUs ---
    @GetMapping("/skus")
    public List<Sku> listSkus(@RequestParam(required = false) Long productId) {
        if (productId != null) return mallService.productSkus(productId);
        return List.of();
    }
    @PostMapping("/skus")
    public Sku createSku(@RequestBody Sku s) { return mallService.saveSku(s); }
    @PutMapping("/skus/{id}")
    public Sku updateSku(@PathVariable Long id, @RequestBody Sku s) { s.setId(id); return mallService.saveSku(s); }
    @DeleteMapping("/skus/{id}")
    public Map<String, Boolean> deleteSku(@PathVariable Long id) { mallService.deleteSku(id); return Map.of("success", true); }

    // --- Cart ---
    @GetMapping("/cart")
    public List<Map<String, Object>> getCart(Authentication auth) {
        return mallService.cart(getUserId(auth));
    }

    @PostMapping("/cart")
    public Map<String, Object> addCart(Authentication auth, @RequestBody Map<String, Object> body) {
        return mallService.addCart(getUserId(auth),
            Long.valueOf(body.get("sku_id").toString()),
            Integer.valueOf(body.get("quantity").toString()));
    }

    @PutMapping("/cart/{id}")
    public Cart updateCart(@PathVariable Long id, @RequestBody Cart c) { c.setId(id); return mallService.saveCart(c); }

    @DeleteMapping("/cart/{id}")
    public Map<String, Boolean> deleteCart(@PathVariable Long id) { mallService.deleteCart(id); return Map.of("success", true); }

    // --- Orders ---
    @PostMapping("/orders/create")
    public Map<String, Object> createOrder(Authentication auth, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> skuIds = ((List<Integer>) body.get("skuIds")).stream().map(Long::valueOf).collect(Collectors.toList());
        return mallService.createOrder(getUserId(auth), skuIds,
            Long.valueOf(body.get("address_id").toString()), (String) body.getOrDefault("remark", ""));
    }

    @GetMapping("/orders/user")
    public List<Map<String, Object>> userOrders(Authentication auth) {
        return mallService.userOrders(getUserId(auth)).stream()
            .map(MallService::orderToMap).collect(Collectors.toList());
    }

    @GetMapping("/orders/detail/{id}")
    public ResponseEntity<Map<String, Object>> orderDetail(@PathVariable Long id) {
        return mallService.findOrder(id).map(o -> ResponseEntity.ok(MallService.orderToMap(o)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/orders/{id}/pay")
    public Map<String, Object> payOrder(@PathVariable Long id) {
        var o = mallService.findOrder(id).orElseThrow();
        o.setStatus("待发货");
        mallService.saveOrder(o);
        return Map.of("success", true);
    }

    @PutMapping("/orders/{id}/ship")
    public Map<String, Object> shipOrder(@PathVariable Long id) {
        var o = mallService.findOrder(id).orElseThrow();
        o.setStatus("待收货");
        mallService.saveOrder(o);
        return Map.of("success", true);
    }

    @PutMapping("/orders/{id}/receive")
    public Map<String, Object> receiveOrder(@PathVariable Long id) {
        var o = mallService.findOrder(id).orElseThrow();
        o.setStatus("已完成");
        mallService.saveOrder(o);
        return Map.of("success", true);
    }

    @PutMapping("/orders/{id}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable Long id) {
        var o = mallService.findOrder(id).orElseThrow();
        o.setStatus("已取消");
        mallService.saveOrder(o);
        return Map.of("success", true);
    }

    @GetMapping("/orders/all")
    public List<Map<String, Object>> allOrders() {
        return mallService.allOrders().stream().map(MallService::orderToMap).collect(Collectors.toList());
    }

    @GetMapping("/orders/merchant")
    public List<Map<String, Object>> merchantOrders(Authentication auth) {
        Long userId = getUserId(auth);
        var m = mallService.findMerchantByUser(userId).orElse(null);
        if (m == null) return List.of();
        Long merchantId = m.getId();
        return mallService.allOrders().stream().filter(o -> {
            try {
                @SuppressWarnings("unchecked")
                var items = (List<Map<String, Object>>) new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(o.getItems(), List.class);
                return items.stream().anyMatch(item ->
                    merchantId.equals(Long.valueOf(item.get("merchantId").toString())));
            } catch (Exception e) { return false; }
        }).map(MallService::orderToMap).collect(Collectors.toList());
    }

    // --- Addresses ---
    @GetMapping("/addresses")
    public List<Address> userAddresses(Authentication auth) {
        return mallService.userAddresses(getUserId(auth));
    }
    @PostMapping("/addresses")
    public Address createAddress(Authentication auth, @RequestBody Address a) {
        a.setUserId(getUserId(auth));
        return mallService.saveAddress(a);
    }
    @PutMapping("/addresses/{id}")
    public Address updateAddress(@PathVariable Long id, @RequestBody Address a) { a.setId(id); return mallService.saveAddress(a); }
    @DeleteMapping("/addresses/{id}")
    public Map<String, Boolean> deleteAddress(@PathVariable Long id) { mallService.deleteAddress(id); return Map.of("success", true); }

    // --- Reviews ---
    @GetMapping("/reviews")
    public List<Review> productReviews(@RequestParam(required = false) Long productId) {
        if (productId != null) return mallService.productReviews(productId);
        return List.of();
    }
    @PostMapping("/reviews")
    public Review createReview(Authentication auth, @RequestBody Review r) {
        r.setUserId(getUserId(auth));
        r.setCreatedAt(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return mallService.saveReview(r);
    }

    // --- AI Chat ---
    @PostMapping("/ai/chat")
    public Map<String, Object> aiChat(@RequestBody Map<String, String> body) {
        return aiChatService.chat(body.get("message"));
    }
}
