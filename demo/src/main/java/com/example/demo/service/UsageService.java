package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.OfficeSupply;
import com.example.demo.entity.UsageRecord;
import com.example.demo.mapper.InventoryMapper;
import com.example.demo.mapper.OfficeSupplyMapper;
import com.example.demo.mapper.UsageRecordMapper;
import com.example.demo.service.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

@Service
public class UsageService {
    private static final String INV_CACHE_KEY = "cache:inventory:list";
    private static final String DASH_CACHE_KEY = "cache:dashboard:stats";

    @Autowired
    private UsageRecordMapper usageMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private OfficeSupplyMapper officeSupplyMapper;
    @Autowired
    private PersonnelService personnelService;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public Page<UsageRecord> page(int pageNum, int pageSize, Integer year, Integer month, String deptName, String personName, String supplyName) {
        QueryWrapper<UsageRecord> qw = new QueryWrapper<>();
        if (year != null) qw.eq("year", year);
        if (month != null) qw.eq("month", month);
        if (deptName != null && !deptName.isEmpty()) qw.eq("dept_name", deptName);
        if (personName != null && !personName.isEmpty()) qw.like("person_name", personName);
        if (supplyName != null && !supplyName.isEmpty()) qw.like("supply_name", supplyName);
        qw.orderByDesc("usage_date");
        return usageMapper.selectPage(new Page<>(pageNum, pageSize), qw);
    }

    @Transactional
    public void save(UsageRecord record) {
        if (record.getUsageDate() == null) {
            record.setUsageDate(new Date());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(record.getUsageDate());
        record.setYear(cal.get(Calendar.YEAR));
        record.setMonth(cal.get(Calendar.MONTH) + 1);

        // Auto-fill supply info by supply_id
        if (record.getSupplyId() != null) {
            OfficeSupply supply = officeSupplyMapper.selectById(record.getSupplyId());
            if (supply != null) {
                record.setSupplyName(supply.getName());
                if (record.getUnit() == null || record.getUnit().isEmpty()) {
                    record.setUnit(supply.getUnit());
                }
                if (record.getUnitPrice() == null) {
                    record.setUnitPrice(supply.getUnitPrice());
                }
            }
        }

        if (record.getQuantity() != null && record.getUnitPrice() != null) {
            BigDecimal maxAmount = new BigDecimal("9999999999.99");
            BigDecimal maxQty = new BigDecimal("99999999.99");
            BigDecimal maxPrice = new BigDecimal("99999999.99");

            if (record.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("数量必须大于0");
            }
            if (record.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("单价不能为负数");
            }
            if (record.getQuantity().compareTo(maxQty) > 0) {
                throw new RuntimeException("数量超出范围，最大为: " + maxQty);
            }
            if (record.getUnitPrice().compareTo(maxPrice) > 0) {
                throw new RuntimeException("单价超出范围，最大为: " + maxPrice);
            }
            BigDecimal amount = record.getQuantity().multiply(record.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(maxAmount) > 0) {
                throw new RuntimeException("金额超出范围，最大为: " + maxAmount + "，当前: " + amount + "（数量×单价）");
            }
            record.setAmount(amount);
        }

        // Validate person exists in personnel table
        if (record.getPersonName() == null || record.getPersonName().isEmpty()) {
            throw new RuntimeException("领用人不能为空");
        }
        if (personnelService.getByName(record.getPersonName()) == null) {
            throw new RuntimeException("领用人 [" + record.getPersonName() + "] 不在人员信息表中，无法领用");
        }

        BigDecimal qty = record.getQuantity() != null ? record.getQuantity() : BigDecimal.ZERO;
        BigDecimal currentStock = inventoryMapper.currentStockOf(record.getSupplyId());
        if (currentStock == null) {
            throw new RuntimeException("物品 [" + record.getSupplyName() + "] 不存在");
        }
        if (currentStock.compareTo(qty) < 0) {
            throw new RuntimeException("物品 [" + record.getSupplyName() + "] 库存不足, 当前库存: " + currentStock + ", 需要: " + qty);
        }

        if (record.getUsageNo() == null) {
            record.setUsageNo(usageMapper.maxUsageNo() + 1);
        }

        usageMapper.insert(record);
        deleteCaches();
    }

    private void deleteCaches() {
        if (redisTemplate != null) {
            redisTemplate.delete(INV_CACHE_KEY);
            redisTemplate.delete(DASH_CACHE_KEY);
        }
    }
}
