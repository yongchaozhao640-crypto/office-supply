package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private void checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("currentUserRole");
        if (!"admin".equals(role)) {
            throw new RuntimeException("无权访问，仅管理员可操作");
        }
    }

    @GetMapping
    public Result<List<User>> list(HttpServletRequest request) {
        checkAdmin(request);
        return Result.ok(userService.listAll());
    }

    @PostMapping
    public Result<?> create(@RequestBody User user, HttpServletRequest request) {
        checkAdmin(request);
        userService.createUser(user);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody User user, HttpServletRequest request) {
        checkAdmin(request);
        userService.updateUser(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        checkAdmin(request);
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");
        if (currentUserId != null && currentUserId.equals(id)) {
            return Result.fail("不能删除自己的账号");
        }
        userService.deleteUser(id);
        return Result.ok();
    }
}
