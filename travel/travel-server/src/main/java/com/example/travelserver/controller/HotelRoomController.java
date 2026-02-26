package com.example.travelserver.controller;

import com.example.travelserver.entity.HotelRoom;
import com.example.travelserver.service.IHotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel-room")
@CrossOrigin
public class HotelRoomController {

    @Autowired
    private IHotelRoomService hotelRoomService;

    @GetMapping("/list")
    public List<HotelRoom> list(@RequestParam Integer hotelId) {
        return hotelRoomService.lambdaQuery().eq(HotelRoom::getHotelId, hotelId).list();
    }

    @GetMapping("/{id}")
    public HotelRoom getById(@PathVariable Integer id) {
        return hotelRoomService.getById(id);
    }
}
