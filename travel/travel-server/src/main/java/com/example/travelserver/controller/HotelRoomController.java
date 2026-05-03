package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.travelserver.entity.HotelRoom;
import com.example.travelserver.service.IHotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel-room")
@CrossOrigin
public class HotelRoomController {

    @Autowired
    private IHotelRoomService hotelRoomService;

    @GetMapping("/list")
    public Object list(
            @RequestParam Integer hotelId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        LambdaQueryWrapper<HotelRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelRoom::getHotelId, hotelId);
        
        if (page != null && pageSize != null) {
            Page<HotelRoom> pageResult = hotelRoomService.page(
                    new Page<>(page, pageSize),
                    wrapper
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageResult.getRecords());
            result.put("total", pageResult.getTotal());
            
            return result;
        } else {
            return hotelRoomService.list(wrapper);
        }
    }

    @GetMapping("/{id}")
    public HotelRoom getById(@PathVariable Integer id) {
        return hotelRoomService.getById(id);
    }

    /**
     * 新增房型
     */
    @PostMapping("/add")
    public boolean add(@RequestBody HotelRoom hotelRoom) {
        return hotelRoomService.save(hotelRoom);
    }

    /**
     * 更新房型
     */
    @PostMapping("/update")
    public boolean update(@RequestBody HotelRoom hotelRoom) {
        return hotelRoomService.updateById(hotelRoom);
    }

    /**
     * 删除房型
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return hotelRoomService.removeById(id);
    }
}
