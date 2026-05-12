package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.OfficeSupply;
import com.example.demo.service.OfficeSupplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/supply")
public class OfficeSupplyController {

    @Autowired
    private OfficeSupplyService supplyService;

    @GetMapping
    public Result<List<OfficeSupply>> list(@RequestParam(required = false) String name) {
        return Result.ok(supplyService.list(name));
    }

    @GetMapping("/{id}")
    public Result<OfficeSupply> get(@PathVariable Integer id) {
        return Result.ok(supplyService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody OfficeSupply supply) {
        supplyService.save(supply);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody OfficeSupply supply) {
        supplyService.update(supply);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        supplyService.delete(id);
        return Result.ok();
    }

    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Integer> ids) {
        supplyService.batchDelete(ids);
        return Result.ok();
    }
}
