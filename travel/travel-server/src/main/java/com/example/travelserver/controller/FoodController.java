package com.example.travelserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.travelserver.entity.Food;
import com.example.travelserver.service.IFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/food")
@CrossOrigin
public class FoodController {

    @Autowired
    private IFoodService foodService;

    @GetMapping("/list")
    public List<Food> list() {
        return foodService.list();
    }

    @GetMapping("/{id}")
    public Food getById(@PathVariable Integer id) {
        return foodService.getById(id);
    }

    // ==================== 超级管理员管理接口 ====================

    /**
     * 获取美食列表（管理端-分页）
     */
    @GetMapping("/admin/list")
    public Map<String, Object> adminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Food> pageResult = foodService.page(new Page<>(page, pageSize));
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        
        return result;
    }

    /**
     * 添加美食
     */
    @PostMapping("/add")
    public boolean add(@RequestBody Food food) {
        return foodService.save(food);
    }

    /**
     * 更新美食
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Food food) {
        return foodService.updateById(food);
    }

    /**
     * 删除美食
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return foodService.removeById(id);
    }
}
