package com.mall.entity;

import jakarta.persistence.*;

@Entity
public class Merchant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String shopName;
    private String shopLogo;
    @Column(length = 2000) private String shopDesc;
    private String contactPhone;
    private String contactName;
    private Integer status;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getShopLogo() { return shopLogo; }
    public void setShopLogo(String shopLogo) { this.shopLogo = shopLogo; }
    public String getShopDesc() { return shopDesc; }
    public void setShopDesc(String shopDesc) { this.shopDesc = shopDesc; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
