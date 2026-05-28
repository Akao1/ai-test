package com.mall.repository;

import com.mall.entity.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    List<Parcel> findByVillageId(Long villageId);
}
