package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.PurchaseRecord;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;

public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {

    @Select("SELECT COALESCE(MAX(purchase_no), 0) FROM purchase_record")
    int maxPurchaseNo();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM purchase_record WHERE year = #{year} AND month = #{month}")
    BigDecimal sumAmountByMonth(int year, int month);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM purchase_record WHERE supply_name = #{supplyName}")
    BigDecimal sumQuantityBySupplyName(String supplyName);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM purchase_record WHERE supply_name = #{supplyName}")
    BigDecimal sumAmountBySupplyName(String supplyName);
}
