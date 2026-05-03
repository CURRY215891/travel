package com.example.travelserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.travelserver.entity.Attraction;
import com.example.travelserver.entity.Food;
import com.example.travelserver.entity.Hotel;
import com.example.travelserver.service.IAttractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attraction")
@CrossOrigin // 允许跨域，防止小程序连不上
public class AttractionController {

    @Autowired
    private IAttractionService attractionService;

    @Autowired
    private com.example.travelserver.service.IHotelService hotelService;
    
    @Autowired
    private com.example.travelserver.service.IFoodService foodService;

    /**
     * 接口 1：获取景点列表（用于首页，支持分类过滤）
     * 访问地址：GET http://localhost:8080/attraction/list?categoryIds=1,3
     */
    @GetMapping("/list")
    public List<Attraction> list(@RequestParam(required = false) String categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            String[] idStrings = categoryIds.split(",");
            Integer[] ids = new Integer[idStrings.length];
            for (int i = 0; i < idStrings.length; i++) {
                ids[i] = Integer.parseInt(idStrings[i].trim());
            }
            return attractionService.lambdaQuery().in(Attraction::getCategoryId, ids).list();
        }
        return attractionService.list();
    }

    /**
     * 接口 2：根据 ID 获取景点详情（用于详情页）
     * 访问地址：GET http://localhost:8080/attraction/1
     */
    @GetMapping("/{id}")
    public Attraction getById(@PathVariable Integer id) {
        return attractionService.getById(id);
    }

    /**
     * 接口 3：关键词搜索（支持分类过滤）
     */
    @GetMapping("/search")
    public List<?> search(@RequestParam String keyword, @RequestParam(required = false) Integer categoryId) {
        if (categoryId != null) {
            if (categoryId == 2) {
                return hotelService.lambdaQuery()
                        .and(wrapper -> wrapper.like(Hotel::getName, keyword).or().like(Hotel::getDescription, keyword))
                        .list();
            } else if (categoryId == 4) {
                return foodService.lambdaQuery()
                        .and(wrapper -> wrapper.like(Food::getName, keyword).or().like(Food::getDescription, keyword))
                        .list();
            }
        }
        
        var query = attractionService.lambdaQuery();
        if (categoryId != null) {
            query.eq(Attraction::getCategoryId, categoryId);
        }
        return query.and(wrapper -> wrapper.like(Attraction::getName, keyword).or().like(Attraction::getDescription, keyword)).list();
    }

    /**
     * 接口 4：按分类获取景点列表
     * 访问地址：GET http://localhost:8080/attraction/listByCategory?categoryId=xxx
     */
    @GetMapping("/listByCategory")
    public List<Attraction> listByCategory(@RequestParam Integer categoryId) {
        return attractionService.lambdaQuery()
                .eq(Attraction::getCategoryId, categoryId)
                .list();
    }

    // ==================== 超级管理员管理接口 ====================

    /**
     * 获取景点列表（管理端-分页）
     */
    @GetMapping("/admin/list")
    public Map<String, Object> adminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Attraction> pageResult = attractionService.page(new Page<>(page, pageSize));
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        
        return result;
    }

    /**
     * 添加景点
     */
    @PostMapping("/add")
    public boolean add(@RequestBody Attraction attraction) {
        return attractionService.save(attraction);
    }

    /**
     * 更新景点
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Attraction attraction) {
        return attractionService.updateById(attraction);
    }

    /**
     * 删除景点
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return attractionService.removeById(id);
    }
}