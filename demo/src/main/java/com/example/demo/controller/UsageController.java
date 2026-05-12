package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.UsageRecord;
import com.example.demo.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    @Autowired
    private UsageService usageService;

    @GetMapping
    public Result<Page<UsageRecord>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) String personName,
            @RequestParam(required = false) String supplyName) {
        return Result.ok(usageService.page(pageNum, pageSize, year, month, deptName, personName, supplyName));
    }

    @PostMapping
    public Result<?> save(@RequestBody UsageRecord record) {
        usageService.save(record);
        return Result.ok();
    }
}
