package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.travelserver.entity.RoomFacility;
import com.example.travelserver.service.IRoomFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room-facility")
@CrossOrigin
public class RoomFacilityController {

    @Autowired
    private IRoomFacilityService roomFacilityService;

    @GetMapping("/list")
    public List<RoomFacility> list(@RequestParam(required = false) Integer type) {
        if (type != null) {
            return roomFacilityService.lambdaQuery().eq(RoomFacility::getType, type).list();
        }
        return roomFacilityService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody RoomFacility facility) {
        return roomFacilityService.save(facility);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody RoomFacility facility) {
        return roomFacilityService.updateById(facility);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return roomFacilityService.removeById(id);
    }
}
