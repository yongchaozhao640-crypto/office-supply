package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UsageRecord;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;

public interface UsageRecordMapper extends BaseMapper<UsageRecord> {

    @Select("SELECT COALESCE(MAX(usage_no), 0) FROM usage_record")
    int maxUsageNo();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM usage_record WHERE year = #{year} AND month = #{month}")
    BigDecimal sumAmountByMonth(int year, int month);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM usage_record WHERE supply_name = #{supplyName}")
    BigDecimal sumQuantityBySupplyName(String supplyName);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM usage_record WHERE supply_name = #{supplyName}")
    BigDecimal sumAmountBySupplyName(String supplyName);
}
