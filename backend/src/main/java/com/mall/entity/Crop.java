package com.mall.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Crop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long parcelId;
    private String type;
    private String variety;
    private Double area;
    private String plantDate;
    private String harvestDate;
    @JsonProperty("yield") private Double yield_;
    private Double income;
    private Double cost;
    private Double profit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParcelId() { return parcelId; }
    public void setParcelId(Long parcelId) { this.parcelId = parcelId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }
    public String getPlantDate() { return plantDate; }
    public void setPlantDate(String plantDate) { this.plantDate = plantDate; }
    public String getHarvestDate() { return harvestDate; }
    public void setHarvestDate(String harvestDate) { this.harvestDate = harvestDate; }
    public Double getYield_() { return yield_; }
    public void setYield_(Double yield_) { this.yield_ = yield_; }
    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    public Double getProfit() { return profit; }
    public void setProfit(Double profit) { this.profit = profit; }
}
