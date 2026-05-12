package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.OfficeSupply;
import com.example.demo.mapper.OfficeSupplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OfficeSupplyService {
    private static final String CACHE_KEY = "cache:supply:list";
    private static final int TTL_MINUTES = 30;
    private static final Random RANDOM = new Random();

    @Autowired
    private OfficeSupplyMapper supplyMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public List<OfficeSupply> list(String name) {
        if (name == null || name.isEmpty()) {
            if (redisTemplate != null) {
                Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
                if (cached != null) {
                    return (List<OfficeSupply>) cached;
                }
            }
            List<OfficeSupply> list = supplyMapper.selectList(new QueryWrapper<OfficeSupply>().orderByAsc("supply_no"));
            if (redisTemplate != null) {
                int ttl = TTL_MINUTES * 60 + RANDOM.nextInt(120);
                redisTemplate.opsForValue().set(CACHE_KEY, list, ttl, TimeUnit.SECONDS);
            }
            return list;
        }
        return supplyMapper.selectList(
            new QueryWrapper<OfficeSupply>().like("name", name).orderByAsc("supply_no"));
    }

    public OfficeSupply getById(Integer id) {
        return supplyMapper.selectById(id);
    }

    public void save(OfficeSupply supply) {
        supplyMapper.insert(supply);
        deleteCache();
    }

    public void update(OfficeSupply supply) {
        supplyMapper.updateById(supply);
        deleteCache();
    }

    public void delete(Integer id) {
        supplyMapper.deleteById(id);
        deleteCache();
    }

    public void batchDelete(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            supplyMapper.deleteBatchIds(ids);
            deleteCache();
        }
    }

    private void deleteCache() {
        if (redisTemplate != null) {
            redisTemplate.delete(CACHE_KEY);
        }
    }
}
