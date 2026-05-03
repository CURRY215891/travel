package com.example.travelserver.controller;

import com.example.travelserver.entity.Hotel;
import com.example.travelserver.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@CrossOrigin
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    @GetMapping("/list")
    public List<Hotel> list() {
        return hotelService.list();
    }

    @GetMapping("/{id}")
    public Hotel getById(@PathVariable Integer id) {
        return hotelService.getById(id);
    }

    /**
     * 更新酒店信息
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Hotel hotel) {
        return hotelService.updateById(hotel);
    }
}
