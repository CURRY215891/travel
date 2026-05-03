package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.travelserver.entity.User;
import com.example.travelserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin // 解决跨域问题
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 授权登录接口
     * 逻辑：优先根据 userId 查询，再根据 openid，最后根据昵称
     */
    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> loginData) {
        String code = loginData.get("code");
        String nickname = loginData.get("nickname");
        String avatar = loginData.get("avatar");
        String userIdStr = loginData.get("userId");

        User user = null;

        // 1. 优先：如果有 userId，直接根据 userId 查询
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                Integer userId = Integer.parseInt(userIdStr);
                user = userService.getById(userId);
            } catch (Exception e) {
                user = null;
            }
        }

        // 2. 其次：根据昵称查询（用于演示的稳定标识）
        if (user == null) {
            String openid = "mock_openid_" + nickname;
            user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        }

        // 3. 最后：如果都找不到，创建新用户
        if (user == null) {
            user = new User();
            user.setOpenid("mock_openid_" + nickname);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setStatus(0);
            user.setCreateTime(LocalDateTime.now());
            userService.save(user);
            System.out.println("新用户注册成功，ID: " + user.getId());
        } else {
            // 检查用户是否被封禁
            if (user.getStatus() != null && user.getStatus() == 1) {
                throw new RuntimeException("账号已被封禁");
            }
            // 更新用户信息
            if (nickname != null && !"微信用户".equals(nickname) && !"".equals(nickname)) {
                user.setNickname(nickname);
            }
            if (avatar != null && !avatar.contains("thirdwx.qlogo.cn") && !"".equals(avatar)) {
                user.setAvatar(avatar);
            }
            userService.updateById(user);
        }
        return user;
    }

    // ==================== 超级管理员用户管理接口 ====================

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getNickname, keyword);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> pageResult = userService.page(new Page<>(page, pageSize), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        
        return result;
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    /**
     * 封禁用户
     */
    @PostMapping("/ban/{id}")
    public boolean banUser(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(1);
            return userService.updateById(user);
        }
        return false;
    }

    /**
     * 解封用户
     */
    @PostMapping("/unban/{id}")
    public boolean unbanUser(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(0);
            return userService.updateById(user);
        }
        return false;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
    }
}
