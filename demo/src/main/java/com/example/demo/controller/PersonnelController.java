package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Personnel;
import com.example.demo.service.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    @Autowired
    private PersonnelService personnelService;

    @GetMapping
    public Result<List<Personnel>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String deptName) {
        return Result.ok(personnelService.list(name, deptName));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Personnel p) {
        personnelService.save(p);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Personnel p) {
        personnelService.update(p);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        personnelService.delete(id);
        return Result.ok();
    }
}
