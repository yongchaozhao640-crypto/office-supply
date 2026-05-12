package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Inventory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;

public interface InventoryMapper extends BaseMapper<Inventory> {

    @Select("SELECT s.supply_no, s.name AS supply_name, s.unit, s.model, s.unit_price, " +
        "COALESCE(p.total_qty, 0) - COALESCE(u.total_qty, 0) AS current_qty, " +
        "COALESCE(p.total_amt, 0) - COALESCE(u.total_amt, 0) AS current_amount, " +
        "COALESCE(s.warning_stock, 5) AS safety_stock, " +
        "COALESCE(inv.need_purchase, '') AS need_purchase, " +
        "COALESCE(inv.opening_qty, 0) AS opening_qty, " +
        "COALESCE(inv.opening_amount, 0) AS opening_amount, " +
        "COALESCE(inv.version, 0) AS version " +
        "FROM office_supply s " +
        "LEFT JOIN (SELECT supply_id, SUM(quantity) AS total_qty, SUM(amount) AS total_amt FROM purchase_record GROUP BY supply_id) p ON s.id = p.supply_id " +
        "LEFT JOIN (SELECT supply_id, SUM(quantity) AS total_qty, SUM(amount) AS total_amt FROM usage_record GROUP BY supply_id) u ON s.id = u.supply_id " +
        "LEFT JOIN inventory inv ON s.supply_no = inv.supply_no " +
        "ORDER BY s.supply_no")
    List<Inventory> selectAllWithStock();

    @Select("SELECT COALESCE(p.total_qty, 0) - COALESCE(u.total_qty, 0) " +
        "FROM office_supply s " +
        "LEFT JOIN (SELECT supply_id, SUM(quantity) AS total_qty FROM purchase_record GROUP BY supply_id) p ON s.id = p.supply_id " +
        "LEFT JOIN (SELECT supply_id, SUM(quantity) AS total_qty FROM usage_record GROUP BY supply_id) u ON s.id = u.supply_id " +
        "WHERE s.id = #{supplyId}")
    BigDecimal currentStockOf(@Param("supplyId") Integer supplyId);

    @Select("SELECT COUNT(*) FROM (" +
        "SELECT COALESCE(p.total_qty, 0) - COALESCE(u.total_qty, 0) AS current_qty, " +
        "COALESCE(s.warning_stock, 5) AS safety_stock " +
        "FROM office_supply s " +
        "LEFT JOIN (SELECT supply_id, SUM(quantity) AS total_qty FROM purchase_record GROUP BY supply_id) p ON s.id = p.supply_id " +
        "LEFT JOIN (SELECT supply_id, SUM(quantity) AS total_qty FROM usage_record GROUP BY supply_id) u ON s.id = u.supply_id " +
        "LEFT JOIN inventory inv ON s.supply_no = inv.supply_no" +
        ") t WHERE t.current_qty <= t.safety_stock AND t.safety_stock > 0")
    int countLowStock();
}
