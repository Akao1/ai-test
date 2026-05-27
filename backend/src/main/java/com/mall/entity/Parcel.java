package com.mall.entity;

import jakarta.persistence.*;

@Entity
public class Parcel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long villageId;
    private Double area;
    private String crop;
    private String owner;
    private String status;
    @Column(length = 2000) private String coords;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getVillageId() { return villageId; }
    public void setVillageId(Long villageId) { this.villageId = villageId; }
    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCoords() { return coords; }
    public void setCoords(String coords) { this.coords = coords; }
}
