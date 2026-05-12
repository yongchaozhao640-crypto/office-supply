package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.ok(dashboardService.stats());
    }
}
