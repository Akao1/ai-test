package com.mall.controller;

import com.mall.entity.*;
import com.mall.service.*;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class PlatformController {

    private final AuthService authService;
    private final PlatformService platformService;

    public PlatformController(AuthService authService, PlatformService platformService) {
        this.authService = authService;
        this.platformService = platformService;
    }

    // --- Auth ---
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        return authService.login(body.get("username"), body.get("password"));
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        return authService.register(body.get("username"), body.get("password"), body.get("nickname"));
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestAttribute("user") Claims claims) {
        return authService.me(Long.parseLong(claims.getSubject()));
    }

    // --- Stats ---
    @GetMapping("/stats/overview")
    public Map<String, Object> overview() {
        return platformService.overview();
    }

    @GetMapping("/stats/crops")
    public List<Map<String, Object>> cropStats() {
        return platformService.cropStats();
    }

    @GetMapping("/stats/income")
    public Map<String, Object> incomeStats() {
        return platformService.incomeStats();
    }

    // --- CRUD endpoints ---
    @GetMapping("/villages")
    public List<Village> listVillages() { return platformService.list(Village.class); }
    @GetMapping("/villages/{id}")
    public ResponseEntity<Village> getVillage(@PathVariable Long id) {
        return platformService.get(Village.class, id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/villages")
    public Village createVillage(@RequestBody Village v) { return platformService.save(Village.class, v); }
    @PutMapping("/villages/{id}")
    public Village updateVillage(@PathVariable Long id, @RequestBody Village v) { v.setId(id); return platformService.save(Village.class, v); }
    @DeleteMapping("/villages/{id}")
    public Map<String, Boolean> deleteVillage(@PathVariable Long id) { platformService.delete(Village.class, id); return Map.of("success", true); }

    @GetMapping("/parcels")
    public List<Parcel> listParcels() { return platformService.list(Parcel.class); }
    @GetMapping("/parcels/{id}")
    public ResponseEntity<Parcel> getParcel(@PathVariable Long id) {
        return platformService.get(Parcel.class, id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/parcels")
    public Parcel createParcel(@RequestBody Parcel p) { return platformService.save(Parcel.class, p); }
    @PutMapping("/parcels/{id}")
    public Parcel updateParcel(@PathVariable Long id, @RequestBody Parcel p) { p.setId(id); return platformService.save(Parcel.class, p); }
    @DeleteMapping("/parcels/{id}")
    public Map<String, Boolean> deleteParcel(@PathVariable Long id) { platformService.delete(Parcel.class, id); return Map.of("success", true); }

    @GetMapping("/households")
    public List<Household> listHouseholds() { return platformService.list(Household.class); }
    @GetMapping("/households/{id}")
    public ResponseEntity<Household> getHousehold(@PathVariable Long id) {
        return platformService.get(Household.class, id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/households")
    public Household createHousehold(@RequestBody Household h) { return platformService.save(Household.class, h); }
    @PutMapping("/households/{id}")
    public Household updateHousehold(@PathVariable Long id, @RequestBody Household h) { h.setId(id); return platformService.save(Household.class, h); }
    @DeleteMapping("/households/{id}")
    public Map<String, Boolean> deleteHousehold(@PathVariable Long id) { platformService.delete(Household.class, id); return Map.of("success", true); }

    @GetMapping("/buildings")
    public List<Building> listBuildings() { return platformService.list(Building.class); }
    @GetMapping("/buildings/{id}")
    public ResponseEntity<Building> getBuilding(@PathVariable Long id) {
        return platformService.get(Building.class, id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/buildings")
    public Building createBuilding(@RequestBody Building b) { return platformService.save(Building.class, b); }
    @PutMapping("/buildings/{id}")
    public Building updateBuilding(@PathVariable Long id, @RequestBody Building b) { b.setId(id); return platformService.save(Building.class, b); }
    @DeleteMapping("/buildings/{id}")
    public Map<String, Boolean> deleteBuilding(@PathVariable Long id) { platformService.delete(Building.class, id); return Map.of("success", true); }

    @GetMapping("/crops")
    public List<Crop> listCrops() { return platformService.list(Crop.class); }

    @GetMapping("/policies")
    public List<Policy> listPolicies() { return platformService.list(Policy.class); }
    @PostMapping("/policies")
    public Policy createPolicy(@RequestBody Policy p) { return platformService.save(Policy.class, p); }
    @PutMapping("/policies/{id}")
    public Policy updatePolicy(@PathVariable Long id, @RequestBody Policy p) { p.setId(id); return platformService.save(Policy.class, p); }
    @DeleteMapping("/policies/{id}")
    public Map<String, Boolean> deletePolicy(@PathVariable Long id) { platformService.delete(Policy.class, id); return Map.of("success", true); }

    @GetMapping("/users")
    public List<User> listUsers() { return platformService.list(User.class); }
    @PostMapping("/users")
    public User createUser(@RequestBody User u) { return platformService.save(User.class, u); }
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User u) { u.setId(id); return platformService.save(User.class, u); }
    @DeleteMapping("/users/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable Long id) { platformService.delete(User.class, id); return Map.of("success", true); }

    @GetMapping("/sysconfig")
    public List<SysConfig> getConfig() { return platformService.list(SysConfig.class); }
    @PutMapping("/sysconfig/{id}")
    public SysConfig updateConfig(@PathVariable Long id, @RequestBody SysConfig c) { c.setId(id); return platformService.save(SysConfig.class, c); }
}
