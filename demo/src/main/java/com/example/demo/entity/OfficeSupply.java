package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("office_supply")
public class OfficeSupply {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer supplyNo;
    private String name;
    private String unit;
    private String model;
    private BigDecimal unitPrice;
    private BigDecimal warningStock;
    private String remark;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSupplyNo() { return supplyNo; }
    public void setSupplyNo(Integer supplyNo) { this.supplyNo = supplyNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getWarningStock() { return warningStock; }
    public void setWarningStock(BigDecimal warningStock) { this.warningStock = warningStock; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
