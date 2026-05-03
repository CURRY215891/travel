package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.travelserver.entity.HotelTag;
import com.example.travelserver.service.IHotelTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel-tag")
@CrossOrigin
public class HotelTagController {

    @Autowired
    private IHotelTagService hotelTagService;

    @GetMapping("/list")
    public List<HotelTag> list() {
        return hotelTagService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody HotelTag tag) {
        return hotelTagService.save(tag);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody HotelTag tag) {
        return hotelTagService.updateById(tag);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return hotelTagService.removeById(id);
    }
}
