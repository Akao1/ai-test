package com.mall.repository;

import com.mall.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByHouseholdId(Long householdId);
}
