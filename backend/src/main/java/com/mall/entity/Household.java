package com.mall.entity;

import jakarta.persistence.*;

@Entity
public class Household {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long villageId;
    private String name;
    private Integer population;
    private Double income;
    private Integer workCount;
    private String address;
    private String phone;
    private String poverty;
    private String policyIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVillageId() { return villageId; }
    public void setVillageId(Long villageId) { this.villageId = villageId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }
    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }
    public Integer getWorkCount() { return workCount; }
    public void setWorkCount(Integer workCount) { this.workCount = workCount; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPoverty() { return poverty; }
    public void setPoverty(String poverty) { this.poverty = poverty; }
    public String getPolicyIds() { return policyIds; }
    public void setPolicyIds(String policyIds) { this.policyIds = policyIds; }
}
