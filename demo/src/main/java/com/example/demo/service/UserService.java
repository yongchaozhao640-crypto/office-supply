package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User login(String username, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }

    public void createUser(User user) {
        User existing = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        userMapper.insert(user);
    }

    public void updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userMapper.updateById(user);
    }

    public void deleteUser(Integer id) {
        User target = userMapper.selectById(id);
        if (target != null && "admin".equals(target.getRole())) {
            long adminCount = userMapper.selectCount(new QueryWrapper<User>().eq("role", "admin"));
            if (adminCount <= 1) {
                throw new RuntimeException("无法删除最后一个管理员");
            }
        }
        userMapper.deleteById(id);
    }

    public User getById(Integer id) {
        return userMapper.selectById(id);
    }

    public List<User> listAll() {
        return userMapper.selectList(new QueryWrapper<User>().orderByDesc("create_time"));
    }
}
