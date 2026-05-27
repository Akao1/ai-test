package com.mall.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.entity.*;
import com.mall.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.mall.repository.AllRepositories.*;

@Component
public class DataLoader {

    private final UserRepository userRepo;
    private final VillageRepository villageRepo;
    private final ParcelRepository parcelRepo;
    private final HouseholdRepository householdRepo;
    private final BuildingRepository buildingRepo;
    private final CropRepository cropRepo;
    private final PolicyRepository policyRepo;
    private final SysConfigRepository configRepo;
    private final MerchantRepository merchantRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final SkuRepository skuRepo;
    private final AddressRepository addressRepo;
    private final PasswordEncoder encoder;
    private final ObjectMapper mapper = new ObjectMapper();

    public DataLoader(UserRepository userRepo, VillageRepository villageRepo,
                      ParcelRepository parcelRepo, HouseholdRepository householdRepo,
                      BuildingRepository buildingRepo, CropRepository cropRepo,
                      PolicyRepository policyRepo, SysConfigRepository configRepo,
                      MerchantRepository merchantRepo, CategoryRepository categoryRepo,
                      ProductRepository productRepo, SkuRepository skuRepo,
                      AddressRepository addressRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.villageRepo = villageRepo;
        this.parcelRepo = parcelRepo;
        this.householdRepo = householdRepo;
        this.buildingRepo = buildingRepo;
        this.cropRepo = cropRepo;
        this.policyRepo = policyRepo;
        this.configRepo = configRepo;
        this.merchantRepo = merchantRepo;
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.skuRepo = skuRepo;
        this.addressRepo = addressRepo;
        this.encoder = encoder;
    }

    @PostConstruct
    public void init() throws Exception {
        if (userRepo.count() > 0) return;

        // Load from data/ directory (relative to project root)
        String dataDir = "data";

        // Platform users
        if (userRepo.count() == 0) {
            var admin = new User(); admin.setUsername("admin"); admin.setPassword(encoder.encode("admin123"));
            admin.setNickname("管理员"); admin.setRole("admin"); admin.setStatus(1);
            admin.setCreatedAt("2026-01-01"); userRepo.save(admin);
            var viewer = new User(); viewer.setUsername("viewer"); viewer.setPassword(encoder.encode("view123"));
            viewer.setNickname("观察员"); viewer.setRole("viewer"); viewer.setStatus(1);
            viewer.setCreatedAt("2026-01-01"); userRepo.save(viewer);

            var merchant1 = new User(); merchant1.setUsername("merchant1"); merchant1.setPassword(encoder.encode("merch123"));
            merchant1.setNickname("商家张三"); merchant1.setRole("merchant"); merchant1.setStatus(1);
            merchant1.setCreatedAt("2026-01-01"); userRepo.save(merchant1);
            var user1 = new User(); user1.setUsername("user1"); user1.setPassword(encoder.encode("user123"));
            user1.setNickname("用户李四"); user1.setRole("user"); user1.setStatus(1);
            user1.setCreatedAt("2026-01-01"); userRepo.save(user1);
        }

        loadJson(dataDir + "/villages.json", Village.class, villageRepo);
        loadJson(dataDir + "/parcels.json", Parcel.class, parcelRepo);
        loadJson(dataDir + "/households.json", Household.class, householdRepo);
        loadJson(dataDir + "/buildings.json", Building.class, buildingRepo);
        loadJson(dataDir + "/crops.json", Crop.class, cropRepo);
        loadJson(dataDir + "/policies.json", Policy.class, policyRepo);
        loadJson(dataDir + "/sysconfig.json", SysConfig.class, configRepo);

        // Mall data
        loadJson(dataDir + "/test/data/merchants.json", Merchant.class, merchantRepo);
        loadJson(dataDir + "/test/data/categories.json", Category.class, categoryRepo);
        loadJson(dataDir + "/test/data/products.json", Product.class, productRepo);
        loadJson(dataDir + "/test/data/skus.json", Sku.class, skuRepo);
        loadJson(dataDir + "/test/data/addresses.json", Address.class, addressRepo);

        System.out.println("Data loaded successfully!");
    }

    private <T> void loadJson(String path, Class<T> type, JpaRepository<T, Long> repo) {
        try {
            var file = new File(path);
            if (!file.exists()) {
                // Try classpath
                InputStream is = getClass().getClassLoader().getResourceAsStream(path);
                if (is == null) return;
                var list = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, type));
                repo.saveAll(list);
                return;
            }
            var list = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, type));
            repo.saveAll(list);
        } catch (Exception e) {
            System.out.println("Skip " + path + ": " + e.getMessage());
        }
    }
}
