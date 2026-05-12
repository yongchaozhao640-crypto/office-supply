package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

@TableName("usage_record")
public class UsageRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer usageNo;
    private Date usageDate;
    private String personName;
    private String deptName;
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
    public Integer getUsageNo() { return usageNo; }
    public void setUsageNo(Integer usageNo) { this.usageNo = usageNo; }
    public Date getUsageDate() { return usageDate; }
    public void setUsageDate(Date usageDate) { this.usageDate = usageDate; }
    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
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
