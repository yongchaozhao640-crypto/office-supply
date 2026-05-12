package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }

        User user = userService.login(username, password);
        String token = jwtUtil.generateToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        userInfo.put("phone", user.getPhone());
        data.put("user", userInfo);

        return Result.ok(data);
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me(HttpServletRequest request) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", request.getAttribute("currentUserId"));
        userInfo.put("username", request.getAttribute("currentUsername"));
        userInfo.put("name", request.getAttribute("currentUserName"));
        userInfo.put("role", request.getAttribute("currentUserRole"));
        return Result.ok(userInfo);
    }
}
