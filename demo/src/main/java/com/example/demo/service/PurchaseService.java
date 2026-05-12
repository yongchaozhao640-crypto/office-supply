package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.OfficeSupply;
import com.example.demo.entity.PurchaseRecord;
import com.example.demo.mapper.OfficeSupplyMapper;
import com.example.demo.mapper.PurchaseRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

@Service
public class PurchaseService {
    private static final String INV_CACHE_KEY = "cache:inventory:list";
    private static final String DASH_CACHE_KEY = "cache:dashboard:stats";

    @Autowired
    private PurchaseRecordMapper purchaseMapper;
    @Autowired
    private OfficeSupplyMapper officeSupplyMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public Page<PurchaseRecord> page(int pageNum, int pageSize, Integer year, Integer month, String purchaser, String supplyName) {
        QueryWrapper<PurchaseRecord> qw = new QueryWrapper<>();
        if (year != null) qw.eq("year", year);
        if (month != null) qw.eq("month", month);
        if (purchaser != null && !purchaser.isEmpty()) qw.like("purchaser", purchaser);
        if (supplyName != null && !supplyName.isEmpty()) qw.like("supply_name", supplyName);
        qw.orderByDesc("purchase_date");
        return purchaseMapper.selectPage(new Page<>(pageNum, pageSize), qw);
    }

    @Transactional
    public void save(PurchaseRecord record) {
        if (record.getPurchaseDate() == null) {
            record.setPurchaseDate(new Date());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(record.getPurchaseDate());
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

        if (record.getPurchaseNo() == null) {
            record.setPurchaseNo(purchaseMapper.maxPurchaseNo() + 1);
        }

        purchaseMapper.insert(record);
        deleteCaches();
    }

    private void deleteCaches() {
        if (redisTemplate != null) {
            redisTemplate.delete(INV_CACHE_KEY);
            redisTemplate.delete(DASH_CACHE_KEY);
        }
    }
}
