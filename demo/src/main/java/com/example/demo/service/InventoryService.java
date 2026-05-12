package com.example.demo.service;

import com.example.demo.entity.Inventory;
import com.example.demo.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryService {
    private static final String CACHE_KEY = "cache:inventory:list";
    private static final int TTL_MINUTES = 10;
    private static final Random RANDOM = new Random();

    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public List<Inventory> list() {
        if (redisTemplate != null) {
            Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cached != null) {
                return (List<Inventory>) cached;
            }
        }
        List<Inventory> list = inventoryMapper.selectAllWithStock();
        if (redisTemplate != null) {
            int ttl = TTL_MINUTES * 60 + RANDOM.nextInt(60);
            redisTemplate.opsForValue().set(CACHE_KEY, list, ttl, TimeUnit.SECONDS);
        }
        return list;
    }

    public int countLowStock() {
        return inventoryMapper.countLowStock();
    }
}
