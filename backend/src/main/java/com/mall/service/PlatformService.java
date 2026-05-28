package com.mall.service;

import com.mall.entity.*;
import com.mall.repository.UserRepository;
import com.mall.repository.VillageRepository;
import com.mall.repository.ParcelRepository;
import com.mall.repository.HouseholdRepository;
import com.mall.repository.BuildingRepository;
import com.mall.repository.CropRepository;
import com.mall.repository.PolicyRepository;
import com.mall.repository.SysConfigRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PlatformService {
    private final VillageRepository villageRepo;
    private final ParcelRepository parcelRepo;
    private final HouseholdRepository householdRepo;
    private final BuildingRepository buildingRepo;
    private final CropRepository cropRepo;
    private final PolicyRepository policyRepo;
    private final SysConfigRepository configRepo;
    private final UserRepository userRepo;

    public PlatformService(VillageRepository villageRepo, ParcelRepository parcelRepo,
                           HouseholdRepository householdRepo, BuildingRepository buildingRepo,
                           CropRepository cropRepo, PolicyRepository policyRepo,
                           SysConfigRepository configRepo, UserRepository userRepo) {
        this.villageRepo = villageRepo;
        this.parcelRepo = parcelRepo;
        this.householdRepo = householdRepo;
        this.buildingRepo = buildingRepo;
        this.cropRepo = cropRepo;
        this.policyRepo = policyRepo;
        this.configRepo = configRepo;
        this.userRepo = userRepo;
    }

    public Map<String, Object> overview() {
        var villages = villageRepo.findAll();
        var households = householdRepo.findAll();
        var parcels = parcelRepo.findAll();
        var buildings = buildingRepo.findAll();
        var map = new LinkedHashMap<String, Object>();
        map.put("villageCount", villages.size());
        map.put("householdCount", households.size());
        map.put("parcelCount", parcels.size());
        map.put("populationTotal", households.stream().mapToInt(Household::getPopulation).sum());
        map.put("avgIncome", households.stream().mapToDouble(Household::getIncome).average().orElse(0));
        map.put("totalArea", parcels.stream().mapToDouble(Parcel::getArea).sum());
        map.put("policyCount", policyRepo.count());
        map.put("buildingCount", buildings.size());
        map.put("poorCount", householdRepo.countPoor());
        return map;
    }

    public List<Map<String, Object>> cropStats() {
        var crops = cropRepo.findAll();
        return crops.stream()
            .collect(Collectors.groupingBy(Crop::getType,
                Collectors.summarizingDouble(c -> c.getYield_() == null ? 0 : c.getYield_())))
            .entrySet().stream().map(e -> {
                var map = new LinkedHashMap<String, Object>();
                map.put("type", e.getKey());
                map.put("area", crops.stream().filter(c -> c.getType().equals(e.getKey()))
                    .mapToDouble(c -> c.getArea() == null ? 0 : c.getArea()).sum());
                map.put("yield", e.getValue().getSum());
                map.put("income", crops.stream().filter(c -> c.getType().equals(e.getKey()))
                    .mapToDouble(c -> c.getIncome() == null ? 0 : c.getIncome()).sum());
                map.put("profit", crops.stream().filter(c -> c.getType().equals(e.getKey()))
                    .mapToDouble(c -> c.getProfit() == null ? 0 : c.getProfit()).sum());
                map.put("count", (int) e.getValue().getCount());
                return map;
            }).collect(Collectors.toList());
    }

    public Map<String, Object> incomeStats() {
        var incomes = householdRepo.findAll().stream().mapToDouble(Household::getIncome).toArray();
        double[] bounds = {3, 5, 8, 12, 20};
        var labels = new String[]{"<3万", "3-5万", "5-8万", "8-12万", "12-20万", ">=20万"};
        var result = new LinkedHashMap<String, Object>();
        for (int i = 0; i < labels.length; i++) result.put(labels[i], 0);
        for (double inc : incomes) {
            if (inc < 3) result.put("<3万", (int) result.get("<3万") + 1);
            else if (inc < 5) result.put("3-5万", (int) result.get("3-5万") + 1);
            else if (inc < 8) result.put("5-8万", (int) result.get("5-8万") + 1);
            else if (inc < 12) result.put("8-12万", (int) result.get("8-12万") + 1);
            else if (inc < 20) result.put("12-20万", (int) result.get("12-20万") + 1);
            else result.put(">=20万", (int) result.get(">=20万") + 1);
        }
        return result;
    }

    public <T> List<T> list(Class<T> type) { return repo(type).findAll(); }
    public <T> Optional<T> get(Class<T> type, Long id) { return repo(type).findById(id); }
    public <T> T save(Class<T> type, T entity) { return repo(type).save(entity); }
    public <T> void delete(Class<T> type, Long id) { repo(type).deleteById(id); }

    @SuppressWarnings("unchecked")
    private <T> JpaRepository<T, Long> repo(Class<T> type) {
        if (type == User.class) return (JpaRepository<T, Long>) userRepo;
        if (type == Village.class) return (JpaRepository<T, Long>) villageRepo;
        if (type == Parcel.class) return (JpaRepository<T, Long>) parcelRepo;
        if (type == Household.class) return (JpaRepository<T, Long>) householdRepo;
        if (type == Building.class) return (JpaRepository<T, Long>) buildingRepo;
        if (type == Crop.class) return (JpaRepository<T, Long>) cropRepo;
        if (type == Policy.class) return (JpaRepository<T, Long>) policyRepo;
        if (type == SysConfig.class) return (JpaRepository<T, Long>) configRepo;
        throw new RuntimeException("Unknown type: " + type);
    }
}
