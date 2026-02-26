package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.travelserver.entity.User;
import com.example.travelserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin // 解决跨域问题
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 授权登录接口
     * 逻辑：根据 OpenID 判断，不存在则注册，存在则更新最新的头像昵称
     */
    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> loginData) {
        String code = loginData.get("code");
        String nickname = loginData.get("nickname");
        String avatar = loginData.get("avatar");

        // 1. 模拟微信登录流程
        // 在真实环境中，应该用 code 换取 openid
        // 这里的漏洞在于：如果直接用 nickname 生成 openid，会导致同名用户账号冲突（甚至被冒充）
        // 改进：优先使用 code 作为唯一标识，如果没有 code 则使用 nickname（仅用于演示）
        String openid = (code != null && !code.isEmpty()) ? "openid_" + code : "mock_openid_" + nickname;

        // 2. 根据 openid 查询数据库
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (user == null) {
            // 3. 情况 A：新用户 -> 执行插入
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setCreateTime(LocalDateTime.now());
            userService.save(user);
            System.out.println("新用户注册成功，ID: " + user.getId());
        } else {
            // 老用户登录时，增加判断
            // 只有当传过来的昵称不是默认值时，才更新数据库
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
}