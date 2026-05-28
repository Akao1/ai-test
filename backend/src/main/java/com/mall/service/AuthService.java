package com.mall.service;

import com.mall.entity.*;
import com.mall.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.mall.entity.*;
import com.mall.repository.UserRepository;
import com.mall.repository.MerchantRepository;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final MerchantRepository merchantRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, MerchantRepository merchantRepo,
                       PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.merchantRepo = merchantRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> login(String username, String password) {
        var user = userRepo.findByUsername(username).orElse(null);
        if (user == null || !encoder.matches(password, user.getPassword()) || user.getStatus() == 0)
            return Map.of("success", false);
        String role = user.getRole();
        var result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("user", userToMap(user));
        result.put("token", jwtUtil.generate(user.getId(), user.getUsername(), role));
        if ("merchant".equals(role) || "admin".equals(role)) {
            var m = merchantRepo.findByUserId(user.getId()).orElse(null);
            if (m != null) result.put("myShop", merchantToMap(m));
        }
        return result;
    }

    public Map<String, Object> register(String username, String password, String nickname) {
        if (userRepo.findByUsername(username).isPresent())
            return Map.of("success", false, "message", "用户名已存在");
        var user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setNickname(nickname);
        user.setRole("user");
        user.setCreatedAt(LocalDate.now().toString());
        userRepo.save(user);
        var result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("user", userToMap(user));
        result.put("token", jwtUtil.generate(user.getId(), user.getUsername(), "user"));
        return result;
    }

    public Map<String, Object> me(Long userId) {
        var user = userRepo.findById(userId).orElse(null);
        if (user == null) return Map.of("error", "not found");
        var result = new LinkedHashMap<String, Object>();
        result.put("user", userToMap(user));
        if ("merchant".equals(user.getRole())) {
            var m = merchantRepo.findByUserId(userId).orElse(null);
            if (m != null) result.put("myShop", merchantToMap(m));
        }
        return result;
    }

    static Map<String, Object> userToMap(User u) {
        return Map.of("id", u.getId(), "username", u.getUsername(),
            "nickname", u.getNickname(), "role", u.getRole(),
            "status", u.getStatus(), "createdAt", u.getCreatedAt());
    }

    static Map<String, Object> merchantToMap(Merchant m) {
        return Map.of("id", m.getId(), "userId", m.getUserId(),
            "shopName", m.getShopName(), "shopLogo", m.getShopLogo(),
            "shopDesc", m.getShopDesc(), "contactPhone", m.getContactPhone(),
            "contactName", m.getContactName(), "status", m.getStatus());
    }
}
