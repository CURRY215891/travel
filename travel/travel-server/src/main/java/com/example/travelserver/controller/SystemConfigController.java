package com.example.travelserver.controller;

import com.example.travelserver.entity.HotelTag;
import com.example.travelserver.entity.RoomFacility;
import com.example.travelserver.service.IHotelTagService;
import com.example.travelserver.service.IRoomFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system-config")
@CrossOrigin
public class SystemConfigController {

    @Autowired
    private IHotelTagService hotelTagService;

    @Autowired
    private IRoomFacilityService roomFacilityService;

    // --- 酒店标签接口 ---
    @GetMapping("/tags")
    public List<HotelTag> listTags() {
        return hotelTagService.list();
    }

    @PostMapping("/tag")
    public boolean addTag(@RequestBody HotelTag tag) {
        return hotelTagService.save(tag);
    }

    @PutMapping("/tag")
    public boolean updateTag(@RequestBody HotelTag tag) {
        return hotelTagService.updateById(tag);
    }

    @DeleteMapping("/tag/{id}")
    public boolean deleteTag(@PathVariable Integer id) {
        return hotelTagService.removeById(id);
    }

    // --- 房间/酒店设施接口 ---
    @GetMapping("/facilities")
    public List<RoomFacility> listFacilities(@RequestParam(required = false) Integer type) {
        if (type != null) {
            return roomFacilityService.lambdaQuery().eq(RoomFacility::getType, type).list();
        }
        return roomFacilityService.list();
    }

    @PostMapping("/facility")
    public boolean addFacility(@RequestBody RoomFacility facility) {
        return roomFacilityService.save(facility);
    }

    @PutMapping("/facility")
    public boolean updateFacility(@RequestBody RoomFacility facility) {
        return roomFacilityService.updateById(facility);
    }

    @DeleteMapping("/facility/{id}")
    public boolean deleteFacility(@PathVariable Integer id) {
        return roomFacilityService.removeById(id);
    }
}
