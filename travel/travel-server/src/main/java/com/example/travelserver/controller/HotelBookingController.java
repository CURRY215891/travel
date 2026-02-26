package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.travelserver.entity.HotelBooking;
import com.example.travelserver.service.IHotelBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@CrossOrigin
public class HotelBookingController {

    @Autowired
    private IHotelBookingService bookingService;
    
    @Autowired
    private com.example.travelserver.mapper.HotelBookingMapper bookingMapper;

    @PostMapping("/add")
    public boolean add(@RequestBody HotelBooking booking) {
        // 1. 检查该房间在指定日期范围内是否已被预订
        QueryWrapper<HotelBooking> qw = new QueryWrapper<>();
        qw.eq("room_id", booking.getRoomId())
          .ne("status", 2) // 排除已取消的订单
          .and(wrapper -> wrapper
              .lt("check_in_date", booking.getCheckOutDate())
              .gt("check_out_date", booking.getCheckInDate())
          );
        
        long count = bookingService.count(qw);
        if (count > 0) {
            return false;
        }

        // 如果 status 为空，默认设为已支付 (1)，因为用户是在订单确认页直接提交
        if (booking.getStatus() == null) {
            booking.setStatus(1);
        }
        return bookingService.save(booking);
    }

    @GetMapping("/user/{userId}")
    public List<HotelBooking> getByUserId(@PathVariable Integer userId) {
        return bookingMapper.selectByUserId(userId);
    }

    @PostMapping("/updateStatus")
    public boolean updateStatus(@RequestBody HotelBooking booking) {
        // 使用实体类接收，Jackson 会自动映射 id 和 status
        return bookingService.updateById(booking);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return bookingService.removeById(id);
    }
}
