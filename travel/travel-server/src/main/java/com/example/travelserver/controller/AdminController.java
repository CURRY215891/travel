package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.travelserver.entity.Admin;
import com.example.travelserver.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @PostMapping("/login")
    public Object login(@RequestBody Admin admin) {
        if (admin.getUsername() == null || admin.getPassword() == null) {
            return returnError("用户名和密码不能为空");
        }
        
        Admin dbAdmin = adminService.getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, admin.getUsername())
                .eq(Admin::getPassword, admin.getPassword()));
        
        if (dbAdmin != null) {
            dbAdmin.setPassword(null); // 安全起见，不返回密码
            return dbAdmin;
        } else {
            return returnError("用户名或密码错误");
        }
    }

    /**
     * 超级管理员创建酒店管理员账号 (商户入驻)
     */
    @PostMapping("/add-merchant")
    public Object addMerchant(@RequestBody Admin merchant) {
        // 简单校验
        if (merchant.getUsername() == null || merchant.getPassword() == null || merchant.getHotelId() == null) {
            return returnError("账号、密码和所属酒店不能为空");
        }
        
        // 检查用户名是否已存在
        long count = adminService.count(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, merchant.getUsername()));
        if (count > 0) {
            return returnError("用户名已存在");
        }
        
        // 强制设为酒店管理员角色
        merchant.setRole(2); 
        boolean saved = adminService.save(merchant);
        if (saved) {
            return "success";
        } else {
            return returnError("创建失败");
        }
    }

    /**
     * 获取商户列表 (role = 2) - 分页
     */
    @GetMapping("/list-merchants")
    public Object listMerchants(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Admin> pageResult = adminService.page(
                new Page<>(page, pageSize),
                new LambdaQueryWrapper<Admin>().eq(Admin::getRole, 2)
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        
        return result;
    }

    /**
     * 删除管理员账号
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return adminService.removeById(id);
    }

    /**
     * 更新商户账号
     */
    @PostMapping("/update-merchant")
    public Object updateMerchant(@RequestBody Admin merchant) {
        if (merchant.getId() == null) {
            return returnError("ID不能为空");
        }
        Admin existing = adminService.getById(merchant.getId());
        if (existing == null) {
            return returnError("商户不存在");
        }
        if (merchant.getNickname() != null) {
            existing.setNickname(merchant.getNickname());
        }
        if (merchant.getHotelId() != null) {
            existing.setHotelId(merchant.getHotelId());
        }
        boolean updated = adminService.updateById(existing);
        if (updated) {
            return "success";
        } else {
            return returnError("更新失败");
        }
    }

    private Map<String, Object> returnError(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("msg", msg);
        return map;
    }
}
