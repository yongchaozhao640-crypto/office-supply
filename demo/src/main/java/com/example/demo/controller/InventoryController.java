package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Inventory;
import com.example.demo.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public Result<List<Inventory>> list() {
        return Result.ok(inventoryService.list());
    }

    @GetMapping("/lowStock")
    public Result<Integer> lowStockCount() {
        return Result.ok(inventoryService.countLowStock());
    }
}
