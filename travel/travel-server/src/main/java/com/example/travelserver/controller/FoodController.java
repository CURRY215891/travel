package com.example.travelserver.controller;

import com.example.travelserver.entity.Food;
import com.example.travelserver.service.IFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
