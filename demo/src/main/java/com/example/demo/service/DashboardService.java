package com.example.demo.service;

import com.example.demo.mapper.InventoryMapper;
import com.example.demo.mapper.PurchaseRecordMapper;
import com.example.demo.mapper.UsageRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class DashboardService {
    private static final String CACHE_KEY = "cache:dashboard:stats";
    private static final int TTL_MINUTES = 5;

    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private PurchaseRecordMapper purchaseMapper;
    @Autowired
    private UsageRecordMapper usageMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> stats() {
        if (redisTemplate != null) {
            Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cached != null) {
                return (Map<String, Object>) cached;
            }
        }

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        Map<String, Object> result = new HashMap<>();
        result.put("lowStockCount", inventoryMapper.countLowStock());
        result.put("monthPurchaseAmount", purchaseMapper.sumAmountByMonth(year, month));
        result.put("monthUsageAmount", usageMapper.sumAmountByMonth(year, month));

        if (redisTemplate != null) {
            int ttl = TTL_MINUTES * 60 + new Random().nextInt(60);
            redisTemplate.opsForValue().set(CACHE_KEY, result, ttl, java.util.concurrent.TimeUnit.SECONDS);
        }
        return result;
    }
}
