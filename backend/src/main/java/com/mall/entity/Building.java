package com.mall.entity;

import jakarta.persistence.*;

@Entity
public class Building {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long householdId;
    private String type;
    private Double area;
    private Integer floors;
    private Integer builtYear;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getHouseholdId() { return householdId; }
    public void setHouseholdId(Long householdId) { this.householdId = householdId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }
    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }
    public Integer getBuiltYear() { return builtYear; }
    public void setBuiltYear(Integer builtYear) { this.builtYear = builtYear; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
