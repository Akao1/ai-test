package com.mall.entity;

import jakarta.persistence.*;

@Entity
public class Village {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double area;
    private Integer population;
    private Integer households;
    @Column(length = 2000) private String desc_;
    @Column(length = 2000) private String coords;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }
    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }
    public Integer getHouseholds() { return households; }
    public void setHouseholds(Integer households) { this.households = households; }
    public String getDesc_() { return desc_; }
    public void setDesc_(String desc_) { this.desc_ = desc_; }
    public String getCoords() { return coords; }
    public void setCoords(String coords) { this.coords = coords; }
}
