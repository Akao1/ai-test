package com.mall.repository;

import com.mall.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface HouseholdRepository extends JpaRepository<Household, Long> {
    List<Household> findByVillageId(Long villageId);
    @Query("SELECT COUNT(h) FROM Household h WHERE h.poverty != '一般户'")
    long countPoor();
}
