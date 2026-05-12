package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.math.BigDecimal;

@TableName("inventory")
public class Inventory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer supplyNo;
    private String supplyName;
    private String unit;
    private String model;
    private BigDecimal unitPrice;
    private BigDecimal openingQty;
    private BigDecimal openingAmount;
    private BigDecimal currentQty;
    private BigDecimal currentAmount;
    private BigDecimal safetyStock;
    private String needPurchase;
    @Version
    private Integer version;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSupplyNo() { return supplyNo; }
    public void setSupplyNo(Integer supplyNo) { this.supplyNo = supplyNo; }
    public String getSupplyName() { return supplyName; }
    public void setSupplyName(String supplyName) { this.supplyName = supplyName; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getOpeningQty() { return openingQty; }
    public void setOpeningQty(BigDecimal openingQty) { this.openingQty = openingQty; }
    public BigDecimal getOpeningAmount() { return openingAmount; }
    public void setOpeningAmount(BigDecimal openingAmount) { this.openingAmount = openingAmount; }
    public BigDecimal getCurrentQty() { return currentQty; }
    public void setCurrentQty(BigDecimal currentQty) { this.currentQty = currentQty; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    public String getNeedPurchase() { return needPurchase; }
    public void setNeedPurchase(String needPurchase) { this.needPurchase = needPurchase; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
