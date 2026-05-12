package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.PurchaseRecord;
import com.example.demo.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public Result<Page<PurchaseRecord>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String purchaser,
            @RequestParam(required = false) String supplyName) {
        return Result.ok(purchaseService.page(pageNum, pageSize, year, month, purchaser, supplyName));
    }

    @PostMapping
    public Result<?> save(@RequestBody PurchaseRecord record) {
        purchaseService.save(record);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        purchaseService.delete(id);
        return Result.ok();
    }

    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Integer> ids) {
        purchaseService.batchDelete(ids);
        return Result.ok();
    }
}
