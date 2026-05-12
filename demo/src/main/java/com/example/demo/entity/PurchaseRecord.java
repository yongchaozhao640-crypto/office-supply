package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

@TableName("purchase_record")
public class PurchaseRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer purchaseNo;
    private Date purchaseDate;
    private String purchaser;
    private Integer supplyId;
    private String supplyName;
    private String unit;
    private String model;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal amount;
    private String remark;
    private Integer year;
    private Integer month;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPurchaseNo() { return purchaseNo; }
    public void setPurchaseNo(Integer purchaseNo) { this.purchaseNo = purchaseNo; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getPurchaser() { return purchaser; }
    public void setPurchaser(String purchaser) { this.purchaser = purchaser; }
    public Integer getSupplyId() { return supplyId; }
    public void setSupplyId(Integer supplyId) { this.supplyId = supplyId; }
    public String getSupplyName() { return supplyName; }
    public void setSupplyName(String supplyName) { this.supplyName = supplyName; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
}
